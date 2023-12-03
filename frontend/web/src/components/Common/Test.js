import { ConfigProvider, Tree, TreeNode } from "antd";
const { DirectoryTree } = Tree;

function test() {
  function createTree(paths) {
    const root = {};

    paths.forEach(({ path, ...attributes }) => {
      const parts = path.split("\\");
      let node = root;
      parts.forEach((part, index) => {
        if (!node[part]) {
          node[part] = {};
        }
        if (index === parts.length - 1) {
          node[part] = { ...node[part], ...attributes, isLeaf: true };
        }
        node = node[part];
      });
    });

    function transformTree(node, path = "") {
      const children = Object.entries(node).map(([key, value]) => {
        const newPath = path ? `${path}/${key}` : key;
        if (value.isLeaf) {
          return { title: key, key: newPath, ...value };
        } else {
          const child = transformTree(value, newPath);
          if (child.length === 1 && !child[0].isLeaf) {
            return { ...child[0], title: key + "." + child[0].title };
          }
          return { title: key, key: newPath, children: child };
        }
      });
      return children;
    }

    return transformTree(root);
  }

  const pathsWithAttributes = [
    {
      path: "src\\main\\resources\\com\\example\\config.xml",
      before: "Before content for config.xml",
      after: "After content for config.xml",
      codeReview: "Code review comments for config.xml",
    },
    {
      path: "src\\main\\resources\\com\\example\\hello\\config.xml",
      before: "Before content for config.xml",
      after: "After content for config.xml",
      codeReview: "Code review comments for config.xml",
    },
    {
      path: "src\\main\\java\\com\\example\\Demo.java",
      before: "Before content for Demo.java",
      after: "After content for Demo.java",
      codeReview: "Code review comments for Demo.java",
    },
    {
      path: "src\\main\\java\\com\\example\\Demo2.java",
      before: "Before content for Demo2.java",
      after: "After content for Demo2.java",
      codeReview: "Code review comments for Demo2.java",
    },
    {
      path: "src\\main\\java\\com\\example2\\Demo3.java",
      before: "Before content for Demo2.java",
      after: "After content for Demo2.java",
      codeReview: "Code review comments for Demo2.java",
    },
  ];

  const antdTreeData = createTree(pathsWithAttributes);

  console.log(antdTreeData);

  return (
    <div>
      <div>
        <ConfigProvider
          theme={{
            token: {
              fontSize: 20,
              paddingXS: 40,
              fontFamily: "Pretendard",
            },
            components: {
              Tree: {
                directoryNodeSelectedBg: "white",
                directoryNodeSelectedColor: "black",
              },
            },
          }}
        >
          <DirectoryTree
            multiple
            defaultExpandAll
            treeData={antdTreeData}
            // icon={<img
            //   style={{ width: 15, padding: 1 }}
            //   src="/file.png"
            //   alt="Custom Icon"
            // />}
          />
        </ConfigProvider>
      </div>
    </div>
  );
}

export default test;
