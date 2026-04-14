import { Navigate, useLocation } from "react-router-dom";
import { authStorage } from "../api/client";

function ProtectedRoute({ children }) {
  const location = useLocation();
  const token = authStorage.getToken();

  if (!token) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return children;
}

export default ProtectedRoute;
