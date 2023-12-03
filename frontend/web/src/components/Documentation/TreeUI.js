import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import styles from "./Tree.module.css";
import { ConfigProvider, Tree, TreeNode } from "antd";
const { DirectoryTree } = Tree;

function TreeUI({treeData}) {

  const navigate = useNavigate();


  const onSelect = (keys, info) => {
    // console.log("Trigger Select", info);
    if (!info.selectedNodes[0].children) {
      navigate(`/detail/${info.selectedNodes[0].docId}`);
    }
  };

  const renderTitle = (nodeData) => {
    return (
      <div className={styles.margin}>
        {nodeData.title}
      </div>
    );
  };

  const onExpand = (keys, info) => {
    // console.log("Trigger Expand", keys, info);
  };
  return (
    <div className={styles.container}>
      <ConfigProvider
        theme={{
          token: {
            fontSize: 20,
            paddingXS: 8,
            fontFamily: "Pretendard",
            colorBgContainer: "var(--tree-background-color)",
            colorText: "var(--main-black-color)"
          },
          components: {
            Tree: {
              directoryNodeSelectedBg: "var(--tree-background-color)",
              directoryNodeSelectedColor: "var(--main-black-color)",
            },
          },
        }}
      >
        <DirectoryTree
          multiple
          onSelect={onSelect}
          onExpand={onExpand}
          treeData={treeData}
          titleRender={renderTitle}
          // icon={<img
          //   style={{ width: 15, padding: 1 }}
          //   src="/file.png"
          //   alt="Custom Icon"
          // />}
        />
      </ConfigProvider>
    </div>
  );
}

export default TreeUI;
