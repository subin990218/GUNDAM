import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { DownOutlined } from "@ant-design/icons";
import { ConfigProvider, Dropdown, Space } from "antd";
import axios from "axios";
import {
  useRecoilValueLoadable,
  useRecoilState,
  useSetRecoilState,
} from "recoil";

import styles from "./SideBar.module.css";
import DarkMode from "../../DarkMode";
import { githubUserQuery } from "../../selectors/githubUserQuery";
import { userIdAtom, userImageURLAtom, userNameAtom } from "../../recoil/user";
import { accessTokenAtom } from "../../recoil/auth";
import { selectedRepositoryState } from "../../recoil/repository";

// const REPO_LIST_URL = "/api/document/repolist";
const REPO_LIST_URL = "https://k9e207.p.ssafy.io/api/document/repolist";
// const REPO_LIST_URL = "http://192.168.30.141:8081/api/document/repolist";

function SideBar({ isOpen, closeSidebar }) {
  const [token, setToken] = useRecoilState(accessTokenAtom);
  const [rawData, setRawData] = useState([]);
  const [selectedRepository, setSelectedRepository] = useRecoilState(
    selectedRepositoryState
  );
  const userData = useRecoilValueLoadable(githubUserQuery);
  const [userIds, setUserIds] = useRecoilState(userIdAtom);
  const [userNames, setUserNames] = useRecoilState(userNameAtom);
  const [userImageURLs, setUserImageURLs] = useRecoilState(userImageURLAtom);
  const [isTransitionEnabled, setIsTransitionEnabled] = useState(false);
  const [isInitial, setIsInitial] = useState(true);

  const fetchRepositoryList = async () => {
    try {
      const response = await axios.get(
        REPO_LIST_URL,
        { params: { userId: userNames } }
        // {
        //   headers: { Authorization: `Bearer d${token}` },
        // }
      );
      const repos = response.data;
      // console.log('리스트 받아오기 테스트', response.data)
      setRawData(repos);
      if (repos.length > 0 && !selectedRepository) {
        setSelectedRepository(repos[0]);
      }
    } catch (error) {
      console.error("레포지토리 리스트 가져오기 에러", error);
    }
  };

  useEffect(() => {
    if (token && userData) {
      fetchRepositoryList();
    }
  }, [token, userData]);

  useEffect(() => {
    if (userData.state === 'hasValue') {
      const user = userData.contents;
      setUserNames(user.username);
      setUserImageURLs(user.avatarUrl);
    }
  }, [userData, setUserIds, setUserNames, setUserImageURLs]);
  

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsTransitionEnabled(true);
      setIsInitial(false);
    }, 800);
    return () => clearTimeout(timer);
  }, []);

  const handleLogout = () => {
    setToken("");
  };

  const renderLoading = () => <div></div>;
  const renderError = (message) => <div>Error: {message}</div>;

  const renderDropdownItems = () => {
    return rawData.map((item, index) => ({
      key: String(index + 1),
      label: (
        <div
          style={{ fontFamily: "Pretendard Variable", fontSize: "1rem" }}
          onClick={() => setSelectedRepository(item)}
        >
          {item}
        </div>
      ),
    }));
  };

  const sidebarClass = `${styles.column} ${isOpen ? styles.open : ""} ${
    isTransitionEnabled ? styles["transition-enabled"] : ""
  } ${isInitial ? "sidebar-initial" : ""}`;

  if (userData.state === "loading") return renderLoading();
  if (userData.state === "hasError")
    return renderError(userData.contents.message);

  return (
    <div className={sidebarClass}>
      <img
        src="/x.png"
        onClick={() => closeSidebar()}
        className={`${styles.x} ${styles.icon}`}
        alt="x button"
      />
      <div>
        {token ? (
          <div className={styles.profileContainer}>
            <img
              src={userData.contents?.avatarUrl}
              alt="User Avatar"
              className={styles.image}
            />
            <p>{userData.contents?.username}</p>
            <div onClick={handleLogout}>로그아웃</div>
          </div>
        ) : null}
      </div>
      <div className={`${styles.container} ${styles.grey}`}>
        <Link to="/" onClick={() => closeSidebar()}>
          <div className={styles.link}>홈</div>
        </Link>
        <Link to="/documentation" onClick={() => closeSidebar()}>
          <div className={styles.link}>코드 문서</div>
        </Link>
        <Link to="/monitoring" onClick={() => closeSidebar()}>
          <div className={styles.link}>협업 모니터링</div>
        </Link>
        <DarkMode />
      </div>
      <div className={styles.dropdown}>
        <div style={{ fontWeight: "var(--font-bold)" }}>Repository</div>
        <ConfigProvider
          theme={{
            token: {
              controlItemBgActive: "#ADADAD",
              colorPrimary: "black",
            },
          }}
        >
          <Dropdown
            menu={{
              items: renderDropdownItems(),
              selectable: true,
              defaultSelectedKeys: ["1"],
            }}
            trigger={["click"]}
          >
            <a onClick={(e) => e.preventDefault()}>
              <Space>
                {selectedRepository}
                <DownOutlined />
              </Space>
            </a>
          </Dropdown>
        </ConfigProvider>
      </div>
    </div>
  );
}

export default SideBar;
