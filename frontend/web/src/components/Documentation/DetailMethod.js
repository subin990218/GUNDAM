import styles from "./DetailMethod.module.css";

function DetailMethod({ method }) {
  const methodId = `method-${method.name}`;
  return (
    <div id={methodId}>
      {/* <div className={styles.title}>{method.name}</div> */}
      <ul className={styles.gap}>
        <li>
          <div className={styles.title}>Return Type</div>
          <div className={styles.container2}>{method.returnType}</div>
        </li>
        <li>
          <div className={styles.title}>Parameter Type</div>
          <table style={{ textAlign: "center" }}>
            <thead>
              <tr>
                <th style={{ textAlign: "center" }}>param number</th>
                <th style={{ textAlign: "center" }}>type</th>
                <th style={{ textAlign: "center" }}>variable name</th>
              </tr>
            </thead>
            <tbody>
              {method.paramList?.map((param, index) => (
                <tr key={index}>
                  <td>{index + 1}</td>
                  <td>{param.type}</td>
                  <td>{param.variable}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </li>
      </ul>
    </div>
  );
}

export default DetailMethod;
