import styles from "./Commits.module.css";

function Commits({ data }) {
  
  if (!data) {
    return (
      <div>
        <div className={styles.title}>Commits</div>
        <div className={styles.card}>
          <div className={styles.flex}>
            <img
              src="/checkmark.png"
              alt="checkmark"
              className={`${styles.img} ${styles.icon}`}
              />
            <div className={styles.tag}>최근 7일간 커밋 횟수</div>
            <div className={styles.tag2}>Loading...</div>
          </div>
          <div className={styles.flex}>
            <img
              src="/time.png"
              alt="time"
              className={`${styles.img2} ${styles.icon}`}
              />
            <div className={styles.tag}>커밋이 가장 많은 시간대</div>
            <div className={styles.tag2}>Loading...</div>
          </div>
          <div className={styles.flex}>
            <img
              src="/human.png"
              alt="user"
              className={`${styles.img2} ${styles.icon}`}
              />
            <div className={styles.tag}>최근 7일간 최다 커밋 유저</div>
            <div className={styles.tag2}>Loading...</div>
          </div>
        </div>
      </div>
    );
  }
  const commitCount = data.commitCount;
  const commitTime = `${data.mostCommitsHour}:00 ~ ${data.mostCommitsHour + 1}:00`;
  const commitUser = data.mostCommit?.mostCommitUser;
  const result = commitUser || "없음";

  return (
    <div>
      <div className={styles.title}>Commits</div>
      <div className={styles.card}>
        <div className={styles.flex}>
          <img
            src="/checkmark.png"
            alt="checkmark"
            className={`${styles.img} ${styles.icon}`}
          />
          <div className={styles.tag}>최근 7일간 커밋 횟수</div>
          <div className={styles.tag2}>{commitCount}</div>
        </div>
        <div className={styles.flex}>
          <img
            src="/time.png"
            alt="time"
            className={`${styles.img2} ${styles.icon}`}
          />
          <div className={styles.tag}>커밋이 가장 많은 시간대</div>
          <div className={styles.tag2}>{commitTime}</div>
        </div>
        <div className={styles.flex}>
          <img
            src="/human.png"
            alt="user"
            className={`${styles.img2} ${styles.icon}`}
          />
          <div className={styles.tag}>최근 7일간 최다 커밋 유저</div>
          <div className={styles.tag2}>{result}</div>
        </div>
      </div>
    </div>
  );
}

export default Commits;
