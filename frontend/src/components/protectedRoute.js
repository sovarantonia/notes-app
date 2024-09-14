import { Navigate } from 'react-router-dom';
import { useUser } from './userContext';

const ProtectedRoute = ({ element: Element, ...rest }) => {
    const { user } = useUser(); // Access the user state from context

    return user ? <Element {...rest} /> : <Navigate to="/login" replace />;
};

export default ProtectedRoute;
