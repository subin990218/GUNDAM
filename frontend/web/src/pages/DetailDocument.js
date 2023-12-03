import { useState, useEffect } from "react";
import styles from "./DetailDocument.module.css";
import { useParams, useLocation } from "react-router-dom";
import Nav from "react-bootstrap/Nav";
import axios from "axios";
import { useRecoilState, useRecoilValue } from "recoil";
import { selectedRepositoryState } from "../recoil/repository";
import Class from "../components/Documentation/Class";
import Method from "../components/Documentation/Method";

const DetailDocument = () => {
  const { id } = useParams();
  // const location = useLocation();
  // const data = location.state;
  const [data, setData] = useState(null);
  let [tab, setTab] = useState(0);
  const selectedRepository = useRecoilValue(selectedRepositoryState);
  // const DETAIL_URL = `http://192.168.30.141:8081/api/document/detail`;
  const DETAIL_URL = `https://k9e207.p.ssafy.io/api/document/detail`;

  const handleClick = () => {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  const getDetail = async () => {
    try {
      const response = await axios.get(DETAIL_URL, { params: { docId: id } });
      if (response.status === 200) {
        setData(response.data);
        console.log("디테일정보", response.data);
      } else {
        console.log("디테일 정보 가져오는데 문제가 있음");
      }
    } catch (error) {
      console.error("디테일 정보 가져오기 에러", error);
    }
  };

  useEffect(() => {
    if (id) {
      getDetail();
    }
  }, [id]);

  if (!data) {
    return <div>Loading...</div>; 
  }

  switch (data.type) {
    case 'class':
      return <Class data={data} handleClick={handleClick}/>;
    case 'method':
      return <Method data={data} handleClick={handleClick}/>;
    default:
      return <div>알 수 없는 타입: {data.type}</div>;
  }
};

export default DetailDocument;
