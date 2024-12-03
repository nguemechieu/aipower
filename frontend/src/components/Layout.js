
import  React from 'react';


import Header from "./Header.js";
import Footer from "./Footer.js";


import {Outlet} from "react-router-dom";
const Layout = () => {
  return (<div className={'flex'}>
              <Header />
              <main> <Outlet/>
              </main><Footer />
      </div>)
};

export default Layout;
