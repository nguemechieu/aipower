// Importing the necessary parts and styles
import "./App.css";
import { Route, Routes } from "react-router-dom";
import Layout from "./components/Layout.js";
import Register from "./components/Register.js";
import TermsOfService from "./components/terms-of-service.js";
import TradeAdviser from "./components/TradeAdviser.js";
import ForgotPassword from "./components/ForgotPassword.js";
import LinkPage from "./components/LinkPage.js";
import Unauthorized from "./components/Unauthorized.js";
import RequireAuth from "./components/RequireAuth.js";
import Home from "./components/Home.js";
import Missing from "./components/Missing.js";
import Editor from "./components/Editor.js";
import Admin from "./components/Admin.js";
import Lounge from "./components/Lounge.js";
import Login from "./components/Login.js";
import { Component } from "react";
import Contact from "./components/Contact.js";
import PersistLogin from "./components/PersistLogin.js";
import News from "./components/News.js";
import AdminUserManagement from "./components/AdminUserManagement.js";
import AccountSummary from "./components/AccountSummary.js";

// Define roles for clarity and reusability
const ROLES = {
  USER: "USER",
  EDITOR: "MANAGER",
  ADMIN: "ADMIN",
    EMPLOYEE: "EMPLOYEE",
    MANAGER: "MANAGER"
};

class About extends Component {
  render() {
    return (
        <section>
          <h1>About</h1>
          <p>
            AiPower is a very powerful and simple{" "}
            <a href="https://www.aipower.com">AI-powered platform</a> that helps
            you create and manage your trades and businesses.
          </p>
        </section>
    );
  }
}



function App() {
  return (
      <Routes>
        {/* Main layout route wrapping all pages */}
        <Route element={<Layout />}>
          {/* Public routes */}
          <Route path="about" element={<About />} />
          <Route path="contact" element={<Contact />} />
          <Route path="admin/manager" element={<AdminUserManagement />} />
            <Route path="account" element={<AccountSummary/>}/>
          <Route path="register" element={<Register />} />
          <Route path="terms-of-service" element={<TermsOfService />} />
          <Route path="tradeadviser" element={<TradeAdviser />} />
          <Route path="forgot-password" element={<ForgotPassword />} />
          <Route path="linkpage" element={<LinkPage />} />
          <Route path="login" element={<Login />} />

          <Route path="news" element={<News/>}/>
          <Route path="unauthorized" element={<Unauthorized />} />

          {/* Protected routes based on user roles */}
          <Route element={<PersistLogin />}>
            <Route element={<RequireAuth allowedRoles={[ROLES.USER, ROLES.EDITOR, ROLES.ADMIN]} />}>
              <Route path="/" element={<Home />} />
              <Route path="missing" element={<Missing />} />
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
