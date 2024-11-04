import { Outlet } from "react-router-dom";
import Header from "./Header.js";
import Footer from "./Footer.js";


const Layout = () => {
  return (
    <>
      <Header />
      <main className="App">
        <div className="flex-grow">
          <Outlet />
        </div>
      </main>
      <Footer />
    </>
  );
};

export default Layout;
