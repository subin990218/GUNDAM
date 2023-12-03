import styles from "./MonitoringPage.module.css";
import MonitorTree from "../components/Monitoring/MonitorTree";
import { onlineUsersState } from "../recoil/onlineUser";
import { useRecoilValue } from "recoil";

const MonitoringPage = () => {
  const Data = useRecoilValue(onlineUsersState);
  // const Data = [
  //   {
  //     onFile: "src/build.gradle",
  //     userName: "DryLeonhard",
  //     status: 'write',
  //   },
  //   {
  //     onFile: "src/example.java",
  //     userName: "subin",
  //     status: "read",
  //   },
  // ];

  function createTree(paths) {
    const root = {};

    paths.forEach(({ onFile, userName, status, imageUrl }) => {
      const parts = onFile.split("/").filter(Boolean);
      let node = root;
      parts.forEach((part, index) => {
        if (!node[part]) {
          node[part] = { children: {} };
        }
        if (index === parts.length - 1) {
          node[part] = {
            ...node[part],
            isLeaf: true,
            userName,
            status,
            imageUrl,
          };
        }
        node = node[part].children;
      });
    });

    // function getStatusImage(status) {
    //   switch (status) {
    //     case "read":
    //       return <img src="/eye.png" alt="Read" className={styles.status}/>;
    //     case "write":
    //       return <img src="/pencil.png" alt="Write" className={styles.status2}/>;
    //     default:
    //       return null;
    //   }
    // }

    function transformTree(node, path = "") {
      return Object.entries(node).map(([key, value]) => {
        const newPath = path ? `${path}/${key}` : key;
        if (value.isLeaf) {
          return {
            title: (
              <>
                <span className={styles.key}>{key}</span>
                <div className={styles.font}>
                  {value.imageUrl && (
                    <img src={value.imageUrl} alt="userimage" className={styles.userImage}/>
                  )}
                  {value.userName && `${value.userName} `}
                </div>
              </>
            ),
            key: newPath,
            isLeaf: true,
          };
        } else {
          return {
            title: key,
            key: newPath,
            children: transformTree(value.children, newPath),
          };
        }
      });
    }

    return transformTree(root);
  }

  const treeData = createTree(Data);

  return (
    <div className={styles.container}>
      <div className={styles.title}>Monitoring</div>
      {treeData.length !== 0 ? (
        <MonitorTree treeData={treeData} />
      ) : (
        <div style={{ marginTop: "2rem" }}>온라인 유저가 없습니다.</div>
      )}
    </div>
  );
};

export default MonitoringPage;
