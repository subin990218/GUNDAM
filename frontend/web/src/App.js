import React, { useState } from "react";
import "./App.css";
import { Routes, Route, BrowserRouter as Router } from "react-router-dom";
import { Provider } from 'react-redux';
import HomePage from "./pages/HomePage";
import MonitoringPage from "./pages/MonitoringPage";
import DocumentationPage from "./pages/DocumentationPage";
import DetailDocument from "./pages/DetailDocument";
import UpperNavBar from "./components/Common/UpperNavBar";
import Setting from "./pages/SettingPage";
import DarkMode from "./DarkMode";
import LoginPage from "./pages/LoginPage";
import Callback from "./pages/Callback";
import { RecoilRoot } from 'recoil'

function App() {



  return (
    <RecoilRoot>
      <Router>
        <UpperNavBar />
        {/* <DarkMode /> */}
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/monitoring" element={<MonitoringPage />} />
          <Route path="/documentation" element={<DocumentationPage />} />
          <Route path="/detail/:id" element={<DetailDocument />} />
          <Route path="/setting" element={<Setting />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/oauth" element={<Callback />} />
        </Routes>
      </Router>
    </RecoilRoot>
  );
}

export default App;
