import { useState, useEffect } from "react";
import styles from "./Class.module.css";
import Prism from "prismjs";
import "prismjs/themes/prism.css";
import Nav from "react-bootstrap/Nav";
import "prismjs/components/prism-java.min";
import SyntaxHighlighter from "react-syntax-highlighter";
import { docco } from "react-syntax-highlighter/dist/esm/styles/hljs";
import { dark } from "react-syntax-highlighter/dist/esm/styles/prism";
import DetailMethod from "./DetailMethod";
import Navigation from "./Navigation";

function Class({ data, handleClick }) {
  let [tab, setTab] = useState(0);
  let theme = localStorage.getItem("theme");

  useEffect(() => {
    Prism.highlightAll();
  }, [tab]);

  const parameterRows =
    data.mplist.length > 0 ? (
      data.mplist.map((item, index) => (
        <tr key={index}>
          <td style={{ textAlign: "center" }}>{item.paramNumber}</td>
          <td style={{ textAlign: "center" }}>{item.type}</td>
          <td style={{ textAlign: "center" }}>{item.variableName}</td>
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
      <div className={styles.name}>[클래스] {data.name}</div>
      <div>
        <div className={styles.title}>1. Code Review</div>
        <div className={styles.container}>{data.codeReview}</div>
      </div>
      <div>
        <div className={styles.title}>2. Navigation</div>
        <div>
          {data.methodList.length !== 0 ? <Navigation methods={data.methodList} /> : <div>클래스 내에 정의된 메서드가 없습니다.</div> }
        </div>
      </div>
      <div>
        <div className={styles.title}>3. Source Code</div>
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
            // useInlineStyles={false}
            style={dark}
            codeTagProps={{ textshadow: "none" }}
            key={tab}
            customStyle={{ fontSize: "var(--font-h3)", backgroundColor: 'var(--main-gray-color)', borderRadius: '10px', border: 'none', boxShadow: 'none' }}
          >
            {tab === 0 ? data.code : data.cleanCode}
          </SyntaxHighlighter>
        ) : (
          <SyntaxHighlighter
            language="java"
            // useInlineStyles={false}
            style={docco}
            codeTagProps={{ textshadow: "none" }}
            key={tab}
            customStyle={{ fontSize: "var(--font-h3)", backgroundColor: 'var(--main-gray-color)', borderRadius: '10px' }}
          >
            {tab === 0 ? data.code : data.cleanCode}
          </SyntaxHighlighter>
        )}
      </div>
      <div>
        <div className={styles.title}>4. Member Fields</div>
        <div className={styles.center}>
          <table style={{ textAlign: "center" }}>
            <thead>
              <tr>
                <th style={{ textAlign: "center" }}>member number</th>
                <th style={{ textAlign: "center" }}>type</th>
                <th style={{ textAlign: "center" }}>variable name</th>
              </tr>
            </thead>
            <tbody>{parameterRows}</tbody>
          </table>
        </div>
      </div>
      <div>
        {data.methodList.map((method, index) => (
          <div key={index}>
            <div className={styles.title} id={`method-${index}`}>
              {5 + index}. {method.name}
            </div>
            <DetailMethod key={index} method={method} />
          </div>
        ))}
      </div>
      <img src="/up.png" className={styles.img} onClick={handleClick} />
    </div>
  );
}

export default Class;
