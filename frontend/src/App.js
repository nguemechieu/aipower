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

const App = () => {
    return (
        <Routes>
            <Route element={<Layout />}>
                {/* Public Routes */}
                <Route path="/" element={<Login />} />
                <Route path="login" element={<Login />} />
                <Route path="register" element={<Register />} />
                <Route path="reset-password" element={<ResetPassword />} />
                <Route path="forgot-password" element={<ForgotPassword />} />
                <Route path="terms-of-service" element={<TermsOfService />} />
                <Route path="help" element={<HelpPage />} />
                <Route path="about" element={<About />} />
                <Route path="docs" element={<SwaggerUIComponent />} />

                {/* Authenticated Routes */}
                <Route element={<PersistLogin />}>
                    <Route path="unauthorized" element={<Unauthorized />} />

                    {/* User Role Routes */}
                    <Route element={<RequireAuth allowedRoles={[ROLES.USER, ROLES.ADMIN]} />}>
                        <Route path="home" element={<Home />} />
                        <Route path="dashboard" element={<Dashboard />} />
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

                {/* Catch-All */}
                <Route path="*" element={<Missing />} />
            </Route>
        </Routes>
    );
};

export default App;
