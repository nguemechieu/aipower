import React from 'react';

import { Link } from "react-router-dom";
import Users from "./Users.js";


const Admin = () => {
  return (
    <section>
      <h1>Admins Page</h1>
      <br />
      <Users />
      <br />
      <div className="flex-grow">
        <Link to="/">Home</Link>
      </div>
    </section>
  );
};

export default Admin;
