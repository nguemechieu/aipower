import  {axiosPublic} from '../api/axios';
import useAuth from './useAuth';

const useRefreshToken = () => {
    const { setAuth } = useAuth();

    return async () => {
        const response = await axiosPublic.get('/api/v3/refresh', {
            withCredentials: true
        });
        setAuth(prev => {
            console.log(JSON.stringify(prev));
            console.log(response.data.accessToken);
            return {...prev, username :response.data.username,role:response.data.role,accessToken: response.data.accessToken}
        });
        return response.data.accessToken;
    };
};

export default useRefreshToken;
