
import  React from 'react';

import { Outlet } from "react-router-dom";
import Header from "./Header.js";
import Footer from "./Footer.js";


const Layout = () => {
  return (<>
          <Header />
      <main className="App">
          <div className="flex-grow">

          <div className={'row'}>
              <div className={'column'}>

                  <Outlet />
              </div>

          </div>
          </div>



      </main> <Footer />
      </>

  );
};

export default Layout;
