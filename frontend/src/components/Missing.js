import React from 'react';

import { Link } from "react-router-dom";

const Missing = () => {
  return (
    <article style={{ padding: "100px" }}>
        <div className="flexGrow">  {/* For centering content */}
      <h1>Oops!</h1>
      <p>Page Not Found</p>

        <Link to="/">Visit Our Homepage</Link>

      </div>
    </article>
  );
};

export default Missing;
