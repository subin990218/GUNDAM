import React, { useEffect, useState } from "react";
import styles from "./Documentation.module.css";
import SearchDocument from "../components/Documentation/SearchDocument";
import TreeUI from "../components/Documentation/TreeUI";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useRecoilState, useRecoilValue } from "recoil";
import { accessTokenAtom } from "../recoil/auth";
import { selectedRepositoryState } from "../recoil/repository";
import { userNameAtom } from "../recoil/user";

const DocumentationPage = () => {
  const [showTree, setShowTree] = useState(true);
  const [results, setResults] = useState([]);
  const navigate = useNavigate();
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const [data, setData] = useState(null);
  const selectedRepository = useRecoilValue(selectedRepositoryState);
  const userName = useRecoilValue(userNameAtom);
  // const DOC_LIST_URL = `/api/document/doclist`;
  const DOC_LIST_URL = `https://k9e207.p.ssafy.io/api/document/doclist`;
  // const DOC_LIST_URL = `http://192.168.30.141:8081/api/document/doclist`;

  // 예시 데이터
  const pathsWithAttributes = [
    {
      createdDate: "2023-11-13",
      filePath:
        "D:/E207/gitlab/plugin/demo/S09P31E207/src/main/java/com/example/demoplugin/kkj/TestCode/fileChangeAction1.java",
      docId: 1,
    },
    {
      createdDate: "2023-11-12",
      filePath:
        "D:/E207/gitlab/plugin/demo/S09P31E207/src/main/java/com/example/demoplugin/kkj/TestCode/fileChangeAction2.java",
      docId: 2,
    },
  ];

  const getDocList = async () => {
    try {
      const response = await axios.get(DOC_LIST_URL, {
        params: {
          userId: userName,
          repoName: selectedRepository,
        },
      });
      if (response.status === 200) {
        setData(response.data);

        // console.log("문서화 정보", response.data);
      } else {
        console.log("문서화 정보 가져오는데 문제가 있음");
      }
    } catch (error) {
      console.error("문서화 정보 가져오기 에러", error);
    }
  };

  function createTree(data) {
    if (!Array.isArray(data) || data.length === 0) {
      return {};
    }
    const dateSortedData = data.reduce((acc, item) => {
      const { createdDate, filePath, docId } = item;
      if (!acc[createdDate]) {
        acc[createdDate] = [];
      }
      acc[createdDate].push({ filePath, docId });
      return acc;
    }, {});

    const trees = {};
    const sortedDates = Object.keys(dateSortedData).sort((a, b) => {
      // 날짜를 비교하기 위해 Date 객체로 변환
      return new Date(b) - new Date(a); // 내림차순 정렬
    });
    sortedDates.forEach((date) => {
      const paths = dateSortedData[date];
      const root = {};

      paths.forEach(({ filePath, docId }) => {
        const normalizedFilePath = filePath.replace(/\\/g, "/");
        const srcIndex = normalizedFilePath.indexOf("/src/");
        const projectFolder = normalizedFilePath.substring(0, srcIndex);
        const basePath = `${projectFolder}/src/main/java/com/example`;

        if (!root[basePath]) {
          root[basePath] = {};
        }

        const relevantPath = normalizedFilePath.substring(
          srcIndex + "/src/main/java/com/example".length
        );
        const parts = relevantPath.split("/").filter((part) => part);
        let node = root[basePath];
        parts.forEach((part, index) => {
          if (!node[part]) {
            node[part] = {};
          }
          if (index === parts.length - 1) {
            node[part] = { ...node[part], docId, isLeaf: true };
          }
          node = node[part];
        });
      });

      function transformTree(node, fullPath = "") {
        const children = Object.entries(node).map(([key, value]) => {
          const newPath = fullPath ? `${fullPath}/${key}` : key;
          const displayTitle = newPath.split("/").slice(-1)[0];

          if (value.isLeaf) {
            return { title: displayTitle, key: newPath, ...value };
          } else {
            const child = transformTree(value, newPath);
            if (child.length === 1 && !child[0].isLeaf) {
              return {
                ...child[0],
                title: displayTitle + "." + child[0].title,
                key: newPath,
              };
            }
            return { title: displayTitle, key: newPath, children: child };
          }
        });
        return children;
      }

      trees[date] = Object.entries(root).map(([basePath, subTree]) => ({
        title: basePath.split("/").slice(-6).join("."),
        key: basePath,
        children: transformTree(subTree),
      }));
    });

    return trees;
  }

  const treeData = createTree(data);
  // console.log(treeData)

  useEffect(() => {
    if (selectedRepository) {
      getDocList();
    }
  }, [selectedRepository]);

  return (
    <div>
      <SearchDocument
        treeData={treeData}
        setShowTree={setShowTree}
        setResults={setResults}
        setShowSuggestions={setShowSuggestions}
        setInputValue={setInputValue}
      />
      <div className={styles.container}>
        {results.length === 0 &&
          Object.keys(treeData).map((date) => (
            <div key={date}>
              <div>{date}</div>
              <TreeUI treeData={treeData[date]} />
            </div>
          ))}
      </div>
    </div>
  );
};

export default DocumentationPage;
