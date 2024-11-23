
import useAuth from "./useAuth";
import axios from "axios";

const useRefreshToken = () => {
  const { setAuth } = useAuth();

  return async () => {
    const response = await axios.post("/api/v3/auth/refresh",
        {

          withCredentials: true
        }

    );
    setAuth((prev) => {
      console.log(JSON.stringify(prev));
      console.log(response.data);
      return { ...prev, accessToken: response?.data?.accessToken ,
        refreshToken: response.data.refreshToken,

        id:response.data.id,id2: response.data.id2 };
    });
    return response.data;
  };
};

export default useRefreshToken;
