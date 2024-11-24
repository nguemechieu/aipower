// Importing the necessary parts and styles
import React from 'react';
// Component Imports
import Layout from './components/Layout.js';
import Register from './components/Register.js';
import TermsOfService from './components/terms-of-service.js';
import TradeAdviser from './components/TradeAdviser.js';
import ForgotPassword from './components/ForgotPassword.js';
import LinkPage from './components/LinkPage.js';
import Unauthorized from './components/Unauthorized.js';
import RequireAuth from './components/RequireAuth.js';
import Home from './components/Home.js';
import Missing from './components/Missing.js';
import Editor from './components/Editor.js';
import Admin from './components/Admin.js';
import Lounge from './components/Lounge.js';
import Login from './components/Login.js';
import PersistLogin from './components/PersistLogin.js';
import News from './components/News.js';
import AdminUserManagement from './components/AdminUserManagement.js';
import AccountSummary from './components/AccountSummary.js';
import Settings from "./components/Settings";
import Chat from "./components/Chat";
import TradingWindow from "./components/Trade.js"
import InvestmentList from "./components/InvestmentList";
import AddInvestmentForm from "./components/AddInvestmentForm";
import PerformanceChart from "./components/PerformanceChart";
import InvestmentPage from "./components/InvestMentPage";
import FriendsPage from "./components/FriendsPage";
import Profile from "./components/Profile";
import HelpPage from "./components/HelpPage";
import ParentComponent from "./components/NotExample";
import SwaggerUIComponent from "./components/SwaggerUIComponent";
import './App.css';
import {Route, Routes} from "react-router-dom";
import Market from "./components/Market";
import ForexMarket from "./components/ForexMarket";
// Define roles for clarity and reusability
const ROLES = {
  USER: 'USER',
  EDITOR: 'EDITOR',
  ADMIN: 'ADMIN',
  MANAGER: 'MANAGER',
  EMPLOYEE: 'EMPLOYEE'
};

// Functional "About" Component
const About = () => (
    <section>
      <h1>About</h1>
      <p>
        AiPower is a powerful and simple{' '}
        <a href="https://www.aipower.com">AI-powered platform</a> that helps you
        create and manage your trades and businesses.
      </p>
    </section>
);

// Functional "Aipower" Component
const Aipower = () => {
  return (
      <div className="aipower-container">
        <h1 className="aipower-heading">AiPower</h1>
        <p className="aipower-paragraph">
          Welcome to <span className="aipower-emphasis">AIPower</span>.
          <br />
          Discover a world of{' '}
          <span className="aipower-emphasis">trade and opportunity</span>.
          <br />
          <em>Make money, make a living, make history.</em>
        </p>
      </div>
  );
};

// Main App Part
function App() {
  return (
      <Routes>
        {/* Main layout route wrapping all pages */}
        <Route element={<Layout />}>
          <Route path={'/'} element={<Login />}/>

          {/* Public routes */}

          <Route path={'forex'} element={<ForexMarket />}/>
          <Route path={'market'} element={<Market />}/>
            <Route path={'help'} element={<HelpPage/>}/>
          <Route path="about" element={<About />} />
          <Route path="nguemechieu/aipower" element={<Aipower />} />
          <Route path="admin/manager" element={<AdminUserManagement />} />
          <Route path="account" element={<AccountSummary />} />
          <Route path="register" element={<Register />} />
          <Route path="terms-of-service" element={<TermsOfService />} />
          <Route path="tradeadviser" element={<TradeAdviser />} />
          <Route path="forgot-password" element={<ForgotPassword />} />
          <Route path="linkpage" element={<LinkPage />} />
            <Route path="login" element={<Login />} />
          <Route path="/docs" element={<SwaggerUIComponent />} />
            <Route path="chat" element={<Chat />} />
          <Route path="notifications" element={<ParentComponent />} />
            <Route path="trade" element={<TradingWindow />} />
            <Route path="admin" element={<AdminUserManagement />} />
          <Route path="investment" element={<InvestmentPage />} />
          <Route path="investment-list" element={<InvestmentList />} />
          <Route path="add-investment" element={<AddInvestmentForm />} />

          <Route path="performance-chart" element={<PerformanceChart />} />
          <Route path="investment-page" element={<InvestmentPage />} />
          <Route path="profile" element={<Profile />} />
          <Route path="friends" element={<FriendsPage />} />
          {/* Catch-all route for unmatched URLs */}

          {/* Private routes with login required */}

            <Route path={'settings'} element={<Settings/>} />
          <Route path="news" element={<News />} />
          <Route path="unauthorized" element={<Unauthorized />} />

          {/* Protected routes based on user roles */}
          <Route element={<PersistLogin />}>
            <Route element={<RequireAuth allowedRoles={[ROLES.USER, ROLES.EDITOR, ROLES.ADMIN]} />}>
              <Route path="home" element={<Home />} />
            </Route>

            <Route element={<RequireAuth allowedRoles={[ROLES.EDITOR]} />}>
              <Route path="editor" element={<Editor />} />
            </Route>

            <Route element={<RequireAuth allowedRoles={[ROLES.ADMIN]} />}>
              <Route path="admin" element={<Admin />} />
            </Route>

            <Route element={<RequireAuth allowedRoles={[ROLES.MANAGER, ROLES.ADMIN]} />}>
              <Route path="manager" element={<Lounge />} />
            </Route>
          </Route>
        </Route>

        {/* Catch-all route for unmatched URLs */}
        <Route path="*" element={<Missing />} />

      </Routes>
  );
}

export default App;
