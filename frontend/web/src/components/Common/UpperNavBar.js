import { useState } from "react";
import styles from "./UpperNavBar.module.css";
import { Link, useLocation } from "react-router-dom";
import SideBar from "./SideBar";
import { useRecoilState } from "recoil";
import { accessTokenAtom } from "../../recoil/auth";

function UpperNavBar() {
  const [token] = useRecoilState(accessTokenAtom); 
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();
  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  // token이 있을 때만 사이드바 토글 버튼을 렌더링
  const renderSidebarToggle = token ? (
    <div>
      <img
        src="/hamburgerbar.png"
        alt="hamburgerbar"
        className={`${styles.hamburger} ${styles.icon}`}
        onClick={toggleSidebar}
      />
      <SideBar closeSidebar={toggleSidebar} isOpen={sidebarOpen} />
    </div>
  ) : null;

  return (
    <div className={styles.horizontal}>
      {location.pathname.includes("/detail/") ? (
        <Link to="/documentation">
          <img alt="arrow" src="/arrow.png" className={`${styles.arrow} ${styles.icon}`} />
        </Link>
      ) : renderSidebarToggle}
      <Link to="/">
        <div className={styles.title}>GUNDAM</div>
      </Link>
    </div>
  );
}

export default UpperNavBar;
