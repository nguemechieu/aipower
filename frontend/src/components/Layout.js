
import  React from 'react';


import Header from "./Header.js";
import Footer from "./Footer.js";


import {Outlet} from "react-router-dom";
const Layout = () => {
  return (<>
              <Header />
              <main><div className={'container'}> <Outlet/></div>
              </main><Footer />
      </>)
};

export default Layout;
