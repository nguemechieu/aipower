import React from "react";
import { Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import Login from "./components/Login";
import Register from "./components/Register";
import TermsOfService from "./components/TermsOfService";
import HelpPage from "./components/HelpPage";
import ForexMarket from "./components/ForexMarket";
import Market from "./components/Market";
import Chat from "./components/Chat";
import Unauthorized from "./components/Unauthorized";
import About from "./components/About";
import Home from "./components/Home";
import AccountSummary from "./components/AccountSummary";
import Settings from "./components/Settings";
import InvestmentList from "./components/InvestmentList";
import AddInvestmentForm from "./components/AddInvestmentForm";
import PerformanceChart from "./components/PerformanceChart";
import SwaggerUIComponent from "./components/SwaggerUIComponent";
import Admin from "./components/Admin";
import ROLES from "./constants/roles";
import RequireAuth from "./components/RequireAuth";
import AdminUserManagement from "./components/AdminUserManagement";
import PersistLogin from "./components/PersistLogin";
import Missing from "./components/Missing";
import InvestmentPage from "./components/InvestmentPage";
import Notification from "./components/Notification";
import Profile from "./components/Profile";
import FriendsPage from "./components/FriendsPage";
import ForgotPassword from "./components/ForgotPassword";
import ResetPassword from "./components/ResetPassword";
import Dashboard from "./components/Dashboard";
import GoogleRedirect from "./components/GoogleRedirect";
import GitHubRedirect from "./components/GitHubRedirect";
import Welcome from "./components/Welcome";
import LogOut from "./components/LogOut";
import StellarAccount from "./components/StellarAccount";
import PrivacyPolicy from "./components/PrivacyPolicy";
import CookiePolicy from "./components/CookiePolicy";
import PressContact from "./components/PressContact";
import Sitemap from "./components/Sitemap";
import Faqs from "./components/Faqs";
import Careers from "./components/Careers";
import Security from "./components/Security";
import LinkPage from "./components/LinkPage";

const App = () => {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* Public Routes */}
        <Route path="/" element={<Login />} />
        <Route
          path="/api/v3/auth/google/callback"
          element={<GoogleRedirect />}
        />
        <Route
          path="api/v3/auth/github/callback"
          element={<GitHubRedirect />}
        />
        <Route path="aipower" element={<Welcome />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="reset-password" element={<ResetPassword />} />
        <Route path="forgot-password" element={<ForgotPassword />} />
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
              <Route path="dashboard" element={<Dashboard />} />

             <Route path="linkpage" element={<LinkPage/>} />
              <Route path="forex" element={<ForexMarket />} />
              <Route path="account" element={<AccountSummary />} />
              <Route path="settings" element={<Settings />} />
              <Route path="notifications" element={<Notification />} />
              <Route path="market" element={<Market />} />
              <Route path="chat" element={<Chat />} />
              <Route path="profile" element={<Profile />} />
              <Route path="friends" element={<FriendsPage />} />

              {/* Nested Investment Routes */}
              <Route path="investment" element={<InvestmentPage />}>
                <Route path="list" element={<InvestmentList />} />
                <Route path="add" element={<AddInvestmentForm />} />
                <Route path="performance" element={<PerformanceChart />} />
              </Route>
            </Route>

            {/* Admin Role Routes */}
            <Route element={<RequireAuth allowedRoles={[ROLES.ADMIN]} />}>
              <Route path="admin" element={<Admin />} />
              <Route path="admin/manager" element={<AdminUserManagement />} />
            </Route>
          </Route>
        </Route>

        {/* Catch-All */}
        <Route path="*" element={<Missing />} />
      </Route>
    </Routes>
  );
};

export default App;
