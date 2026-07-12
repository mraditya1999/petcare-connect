/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect } from "react";
import { useAppDispatch } from "@/app/hooks";
import { googleLoginUser } from "@/features/auth/authThunk";
import { useNavigate } from "react-router-dom";
import ShowToast from "@/components/shared/ShowToast";
import { LoadingSpinner } from "../ui/LoadingSpinner";
import { ROUTES } from "@/utils/constants";

export default function GoogleCallback() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get("code");
    const returnedState = urlParams.get("state");
    const storedState = sessionStorage.getItem("google_oauth_state");

    if (!code) {
      ShowToast({
        description: "Google login failed: No code found",
        type: "error",
      });
      navigate(ROUTES.LOGIN);
      return;
    }

    if (returnedState !== storedState) {
      ShowToast({ description: "Invalid OAuth state", type: "error" });
      navigate(ROUTES.LOGIN);
      return;
    }

    const stateToSend = returnedState ?? storedState ?? undefined;
    sessionStorage.removeItem("google_oauth_state");
    dispatch(googleLoginUser({ code, state: stateToSend, navigate }));
  }, []);

  return <LoadingSpinner size={62} />;
}
