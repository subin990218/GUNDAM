import styles from "./RecentChange.module.css";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function RecentChange({ data }) {

  const navigate = useNavigate();

  const onSelect = (docId) => {
      navigate(`/detail/${docId}`);
  };


  return (
    <div>
      <div className={styles.title}>Today's changes</div>
      <div className={styles.card}>
        {data.length === 0 ? (
          <div style={{fontWeight:'var(--font-medium)'}}>변경된 문서 없음</div> 
        ) : (
          data.map((File, index) => {
            return (
              <div className={styles.file} key={index} onClick={()=>onSelect(File.docId)}>
                {File.name}
              </div>
            );
          })
        )}
        <div className={styles.flex}></div>
      </div>
    </div>
  );
}

export default RecentChange;
