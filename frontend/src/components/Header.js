import  React from 'react';

import { Link } from "react-router-dom";

const Header = () => {
  return (
    <h1>
      <h1>AiPower</h1>
      <div className="row">
        <div className="col-md-12">

          <img  src="../aipower2.ico"
                alt="AiPower logo"
                className="logo"
                style={{ width: "100px", margin: "10px" }}
                title="AiPower Logo"


          />


          <ul className="navbar-nav">
            <li className="nav-item">
              <Link className="nav-link" to="/">
                Home
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/about">
                About
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/contact">
                Contact
              </Link>
            </li>
          </ul>
        </div>
        </div>

    </h1>)};

export default Header;
