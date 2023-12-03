import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useRecoilState } from "recoil";
import { accessTokenAtom } from "../recoil/auth";

const Callback = () => {
  const navigate = useNavigate();
  const location = new URL(window.location.href);
  const code = location.searchParams.get("token");
  const [token, setToken] = useRecoilState(accessTokenAtom);

  useEffect(() => {
    setToken(code);
    navigate("/", { state: code });
  }, []);

  return <div></div>;
};

export default Callback;
