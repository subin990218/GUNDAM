import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import styles from "./HomePage.module.css";
import RecentChange from "../components/Home/RecentChange";
import Commits from "../components/Home/Commits";
import Online from "../components/Home/Online";
import { accessTokenAtom } from "../recoil/auth";
import { useRecoilState, useRecoilValue } from "recoil";
import { selectedRepositoryState } from "../recoil/repository";
import { onlineUsersState } from "../recoil/onlineUser";
import axios from "axios";
import { userNameAtom } from "../recoil/user";

const HomePage = () => {
  const [token, setToken] = useRecoilState(accessTokenAtom);
  const [userNames, setUserNames] = useRecoilState(userNameAtom);
  const [onlineUsers, setOnlineUsers] = useRecoilState(onlineUsersState);
  const location = useLocation();
  const navigate = useNavigate();
  const selectedRepository = useRecoilValue(selectedRepositoryState);
  const [data, setData] = useState(null);
  const [onlineUser, setOnlineUser] = useState([]);
  const [today, setToday] = useState([]);

  // const REPO_INFO_URL = `/api/git/repoinfo`;
  const REPO_INFO_URL = `https://k9e207.p.ssafy.io/api/git/repoinfo`;
  // const RECENT_URL = `http://192.168.30.141:8081/api/document/recent`;
  const RECENT_URL = `https://k9e207.p.ssafy.io/api/document/recent`;
  const STATUS_URL = `https://k9e207a.p.ssafy.io/api/socket/status/${userNames}/${selectedRepository}.git`;
  // useEffect(()=>{
  //   async function loginUser(id, accessToken) {
  //     try {
  //       const response = await axios.post("http://k9e207.p.ssafy.io:8081 :8081/git/repolist", {
  //       name: id,
  //       token : accessToken,

  //       });

  //       if (response.status === 200) {
  //         console.log(response.data, githubId);

  //       } else {
  //         console.log("로그인 실패");
  //       }
  //     } catch (error) {
  //       console.error("에러가 발생:", error);
  //     }
  //   }

  //   loginUser(githubId, accessToken);

  // },[accessToken])

  const repositoryChange = async () => {
    try {
      const response = await axios.post(
        REPO_INFO_URL,
        { name: userNames, token: token },
        { params: { repository: selectedRepository } }
      );
      if (response.status === 200) {
        setData(response.data);
        // console.log("상세정보", response.data);
      } else {
        console.log("레포지토리 정보 가져오는데 문제가 있음");
      }
    } catch (error) {
      console.error("레포지토리 정보 가져오기 에러", error);
    }
  };
  const getOnline = async () => {
    try {
      const response = await axios.get(STATUS_URL);
      if (response.status === 500) {
        setOnlineUser([]);
        // console.log('online',response.data);
      }
      if (response.status === 200) {
        const onlineUsersWithImages = response.data.map((user) => ({
          ...user,
          imageUrl: `https://github.com/${user.userName}.png`,
        }));
        setOnlineUser(onlineUsersWithImages);
        setOnlineUsers(onlineUsersWithImages);
        console.log("online", onlineUsersWithImages);
      } else {
        console.log("online 정보 가져오는데 문제가 있음");
      }
    } catch (error) {
      console.error("online 정보 가져오기 에러", error);
    }
  };
  const getRecent = async () => {
    try {
      const response = await axios.get(RECENT_URL, {
        params: {
          userId: userNames,
          repoName: selectedRepository,
        },
      });
      if (response.status === 200) {
        // console.log("오늘", response.data);
        setToday(response.data);
      } else {
        console.log("오늘 정보 가져오는데 문제가 있음");
      }
    } catch (error) {
      console.error("오늘 정보 가져오기 에러", error);
    }
  };

  useEffect(() => {
    if (token) {
      if (selectedRepository) {
        repositoryChange();
        getOnline();
        getRecent();
      }
      // console.log(selectedRepository)
    } else {
      navigate("/login");
    }
  }, [token, selectedRepository]);

  return (
    <div className={styles.container}>
      <RecentChange data={today}/>
      <Commits data={data} />
      <Online onlineUser={onlineUser} />
    </div>
  );
};

export default HomePage;
