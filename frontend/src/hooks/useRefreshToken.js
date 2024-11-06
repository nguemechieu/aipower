
import useAuth from "./useAuth";
import axios from "axios";

const useRefreshToken = () => {
  const { setAuth } = useAuth();

  return async () => {
    const response = await axios.get("/api/v3/auth/refresh",
        {

          withCredentials: true
        }

    );
    setAuth((prev) => {
      console.log(JSON.stringify(prev));
      console.log(response.data);
      return { ...prev, token: response.data.token ,role: response.data.role };
    });
    return response.data;
  };
};

export default useRefreshToken;
