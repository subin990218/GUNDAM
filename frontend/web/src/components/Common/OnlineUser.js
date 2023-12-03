import styles from "./OnlineUser.module.css";

function OnlineUser({ onlineUser }) {
  return (
    <div className={styles.column}>
      <div className={styles.horizontal}>
        <img src={onlineUser.imageUrl} className={styles.img} alt="onlineuserimage"/>
        {onlineUser.status === 'OFF' ?<div className={styles.circle2}></div> : <div className={styles.circle}></div>}
      </div>
      <div className={styles.name}>{onlineUser.userName}</div>
    </div>
  );
}

export default OnlineUser;
