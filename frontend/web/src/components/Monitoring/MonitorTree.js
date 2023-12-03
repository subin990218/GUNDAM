import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import styles from "./MonitorTree.module.css";
import { ConfigProvider, Tree, TreeNode } from "antd";
const { DirectoryTree } = Tree;


function MonitorTree({treeData}) {
    return ( <div className={styles.container}>
        <ConfigProvider
          theme={{
            token: {
              fontSize: 20,
              paddingXS: 40,
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
            defaultExpandAll
            treeData={treeData}
          />
        </ConfigProvider>
      </div> );
}

export default MonitorTree;