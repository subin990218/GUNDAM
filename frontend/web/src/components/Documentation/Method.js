import { useState } from "react";
import styles from "./Method.module.css";
import "./Method.css";
import "highlight.js/styles/github.css";
import Prism from "prismjs";
import "prismjs/themes/prism.css";
import { useEffect } from "react";
import Nav from "react-bootstrap/Nav";
import "prismjs/components/prism-java.min";
import SyntaxHighlighter from "react-syntax-highlighter";
import { docco } from "react-syntax-highlighter/dist/esm/styles/hljs";
import { dark } from "react-syntax-highlighter/dist/esm/styles/prism";

function Method({ data, handleClick }) {
  let [tab, setTab] = useState(0);
  let theme = localStorage.getItem("theme");

  useEffect(() => {
    Prism.highlightAll();
  }, [tab]);

  const parameterRows =
    data.mplist.length > 0 ? (
      data.mplist.map((item, index) => (
        <tr key={index}>
          <td style={{ textAlign: "center" }}>{index + 1}</td>
          <td style={{ textAlign: "center" }}>{item.type}</td>
          <td style={{ textAlign: "center" }}>{item.variable}</td>
        </tr>
      ))
    ) : (
      <tr>
        <td style={{ textAlign: "center" }} colSpan="3">
          없음
        </td>
      </tr>
    );

  return (
    <div className={styles.column}>
      <div className={styles.name}>[메서드] {data.name}</div>
      <div>
        <div className={styles.title}>1. Code Review</div>
        <div className={styles.container}>{data.codeReview}</div>
      </div>
      <div>
        <div className={styles.title}>2. Source Code</div>
        <div className={styles.card}>
          <Nav
            variant="underline"
            defaultActiveKey="link-0"
            className={styles.nav}
          >
            <Nav.Item className={styles.width}>
              <Nav.Link
                eventKey="link-0"
                onClick={() => setTab(0)}
                className={styles.black}
                style={{ color: "var(--main-black-color" }}
              >
                Commented Code
              </Nav.Link>
            </Nav.Item>
            <Nav.Item className={styles.width}>
              <Nav.Link
                eventKey="link-1"
                onClick={() => setTab(1)}
                className={styles.black}
                style={{ color: "var(--main-black-color" }}
              >
                Clean Code
              </Nav.Link>
            </Nav.Item>
          </Nav>
        </div>
        {theme === "dark" ? (
          <SyntaxHighlighter
            language="java"
            style={dark}
            key={tab}
            codeTagProps={{ textshadow: "none" }}
            customStyle={{ fontSize: "var(--font-h3)", backgroundColor: 'var(--main-gray-color)', borderRadius: '10px', border: 'none', boxShadow: 'none' }}
          >
            {tab === 0 ? data.code : data.cleanCode}
          </SyntaxHighlighter>
        ) : (
          <SyntaxHighlighter
            language="java"
            style={docco}
            key={tab}
            codeTagProps={{ textshadow: "none" }}
            customStyle={{ fontSize: "var(--font-h3)", backgroundColor: 'var(--main-gray-color)', borderRadius: '10px' }}
          >
            {tab === 0 ? data.code : data.cleanCode}
          </SyntaxHighlighter>
        )}
      </div>
      <div>
        <div className={styles.title}>3. Return Type</div>
        <div className={styles.container2}>{data.returnType}</div>
      </div>
      <div>
        <div className={styles.title}>4. Parameter Type</div>
        <div className={styles.center}>
          <table style={{ textAlign: "center" }}>
            <thead>
              <tr>
                <th style={{ textAlign: "center" }}>param number</th>
                <th style={{ textAlign: "center" }}>type</th>
                <th style={{ textAlign: "center" }}>variable name</th>
              </tr>
            </thead>
            <tbody>{parameterRows}</tbody>
          </table>
        </div>
      </div>
      <img src="/up.png" className={styles.img} onClick={handleClick} alt="up"/>
    </div>
  );
}

export default Method;
