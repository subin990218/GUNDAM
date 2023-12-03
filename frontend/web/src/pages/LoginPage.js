import styles from "./LoginPage.module.css";

function LoginPage() {
  const AUTHORIZATION_CODE_URL =`https://k9e207.p.ssafy.io/oauth2/authorization/github`;
  const fetchAuthCode = () => {
    window.location.assign(AUTHORIZATION_CODE_URL);
  };
  return (
    <div className={styles.container}>
      <div className={styles.login}>로그인</div>
      <img
        src="/button.png"
        alt="login button"
        onClick={fetchAuthCode}
        className={styles.img}
      />
    </div>
  );
}

export default LoginPage;
