import React from "react";

import { Route, Routes } from "react-router-dom";
import ROLES from "./constants/roles";
import Layout from "./components/Layout";
import Login from "./components/Login";
import Register from "./components/Register";
import ForgotPassword from "./components/ForgotPassword";
import ResetPassword from "./components/ResetPassword";
import TermsOfService from "./components/TermsOfService";
import HelpPage from "./components/HelpPage";
import About from "./components/About";
import SwaggerUIComponent from "./components/SwaggerUIComponent";
import LogOut from "./components/LogOut";
import StellarAccount from "./components/StellarAccount";
import PrivacyPolicy from "./components/PrivacyPolicy";
import CookiePolicy from "./components/CookiePolicy";
import PressContact from "./components/PressContact";
import Sitemap from "./components/Sitemap";
import Faqs from "./components/Faqs";
import Careers from "./components/Careers";

import PersistLogin from "./components/PersistLogin";
import Unauthorized from "./components/Unauthorized";
import RequireAuth from "./components/RequireAuth";
import Home from "./components/Home";
import LinkPage from "./components/LinkPage";

import Market from "./components/Market";
import Chat from "./components/Chat";
import Profile from "./components/Profile";

import Admin from "./components/Admin";
import Missing from "./components/Missing";
import Security from "./components/Security";
import Settings from "./components/Settings";
import Dashboard from "./components/Dashboard";
import Welcome from "./components/Welcome";
import UserProfile from "./components/UserProfile";
import GoogleRedirect from "./components/GoogleRedirect";
import GitHubRedirect from "./components/GitHubRedirect";



function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* Public Routes */}
        <Route path="/" element={<Login />} />
       <Route path={'/api/v3/auth/google/callback'} element={<GoogleRedirect />} />
          <Route path={'/api/v3/auth/github/callback'} element={<GitHubRedirect />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="aipower" element={<Welcome />} />
        <Route path="resetpassword" element={<ResetPassword />} />
        <Route path="forgotpassword" element={<ForgotPassword />} />
        <Route path="terms-of-service" element={<TermsOfService />} />
        <Route path="help" element={<HelpPage />} />
        <Route path="about" element={<About />} />
        <Route path="docs" element={<SwaggerUIComponent />} />
        <Route path="logout" element={<LogOut />} />
        <Route path="stellar/accounts" element={<StellarAccount />} />
        <Route path="privacy-policy" element={<PrivacyPolicy />} />
        <Route path={"cookie-policy"} element={<CookiePolicy />} />
        <Route path="press-contact" element={<PressContact />} />
        <Route path="sitemap" element={<Sitemap />} />
        <Route path={"faqs"} element={<Faqs />} />
        <Route path="careers" element={<Careers />} />
        <Route path="security" element={<Security />} />
        <Route path="documentation" element={<SwaggerUIComponent />} />

        {/* Authenticated Routes */}
        <Route element={<PersistLogin />}>
          <Route path="unauthorized" element={<Unauthorized />} />

          {/* User Role Routes */}
          <Route
            element={<RequireAuth allowedRoles={[ROLES.USER, ROLES.ADMIN]} />}
          >
            <Route path="/" element={<Home />}>
                <Route path="user" element={<UserProfile />} />
              <Route path="dashboard" element={<Dashboard />} />

              <Route path="linkpage" element={<LinkPage />} />

              <Route path="settings" element={<Settings />} />

              <Route path="market" element={<Market />} />
              <Route path="chat" element={<Chat />} />
              <Route path="profile" element={<Profile />} />

              {/* Nested Investment Routes */}
            </Route>

            {/* Admin Role Routes */}
            <Route element={<RequireAuth allowedRoles={[ROLES.ADMIN]} />}>
              <Route path="admin" element={<Admin />} />
            </Route>
          </Route>
        </Route>

        {/* Catch-All */}
        <Route path="*" element={<Missing />} />
      </Route>
    </Routes>
  );
}

export default App;
