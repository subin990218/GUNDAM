import styles from "./SearchDocument.module.css";
import React, { useState } from "react";
import axios from "axios";
import { debounce } from "lodash";
import { useNavigate } from "react-router-dom";

function SearchDocument({ treeData, setShowTree, setResults }) {
  const [inputValue, setInputValue] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [selectedItem, setSelectedItem] = useState(null);
  const [showSuggestions, setShowSuggestions] = useState(true);
  const navigate = useNavigate();

  const formatDate = (dateString) => {
    const parts = dateString.split("-");
    return `${parts[1]}/${parts[2]}`;
  };

  const searchTree = (query, tree) => {
    let result = [];
    const lowerCaseQuery = query.toLowerCase();

    const dfs = (node, currentDate) => {
      if (node.isLeaf && node.title.toLowerCase().startsWith(lowerCaseQuery)) {
        result.push({
          title: node.title,
          key: node.key,
          date: formatDate(currentDate),
          docId: node.docId,
        });
      }
      // console.log(result)
      if (node.children) {
        node.children.forEach((child) => dfs(child, currentDate));
      }
    };

    Object.keys(tree).forEach((date) => {
      tree[date].forEach((rootNode) => {
        dfs(rootNode, date);
      });
    });

    return result;
  };

  const handleInputChange = debounce((value) => {
    if (value.trim() === "") {
      setSuggestions([]);
      setSelectedItem(null);
      setShowTree(true);
      setResults([]);
    } else {
      const result = searchTree(value, treeData);
      setSuggestions(result);
      // setSelectedItem(null);
      // setShowTree(false);
      setShowSuggestions(true);
    }
  }, 300);

  const handleSearch = () => {
    if (inputValue.trim() === "") return;
    const result = searchTree(inputValue, treeData);
    setSelectedItem(result);
    setSuggestions([]);
    setShowSuggestions(false);
    setShowTree(false);
    setResults(result);
  };

  const toDetail = (item) => {
    setShowSuggestions(false);
    console.log(item);
    navigate(`/detail/${item.docId}`);
  };

  return (
    <div className={styles.container}>
      <div className={styles.title}>Documentation</div>
      <div className={styles.form}>
        <input
          placeholder="파일명 입력"
          className={styles.input}
          value={inputValue}
          onChange={(e) => {
            setInputValue(e.target.value);
            handleInputChange(e.target.value);
          }}
        ></input>
        <button className={styles.button} onClick={handleSearch}>
          Search
        </button>
      </div>
      {showSuggestions && inputValue &&
        (suggestions.length > 0 ? (
          <div className={styles.suggestedItemList}>
            {suggestions.map((suggestion, index) => (
              <div
                key={index}
                className={styles.flex}
                onClick={() => toDetail(suggestion)}
              >
                <div>{suggestion.date}</div>
                <img
                  src="/file.png"
                  className={`${styles.file} ${styles.icon}`}
                  alt="file"
                />
                <div className={styles.suggestedTitle}>{suggestion.title}</div>
              </div>
            ))}
          </div>
        ) : (
          <div className={styles.suggestedItemList}>검색 결과 없음</div>
        ))}

      {selectedItem ? (
        selectedItem.length > 0 ? (
          <div className={styles.selectedItemList}>
            {selectedItem.map((item, index) => (
              <div
                key={index}
                onClick={() => toDetail(item)}
                className={`${styles.selectedItem} ${styles.flex}`}
              >
                <div>{item.date}</div>
                <img src="/file.png" className={styles.icon} alt="file" />
                <div className={styles.suggestedTitle}>{item.title}</div>
              </div>
            ))}
          </div>
        ) : (
          <div className={styles.result}>검색 결과 없음</div>
        )
      ) : null}
    </div>
  );
}

export default SearchDocument;
