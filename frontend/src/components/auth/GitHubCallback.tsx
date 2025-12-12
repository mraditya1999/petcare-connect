import { useEffect } from "react";
import { useAppDispatch } from "@/app/hooks";
import { githubLoginUser } from "@/features/auth/authThunk";
import { useNavigate } from "react-router-dom";
import ShowToast from "@/components/shared/ShowToast";
import { LoadingSpinner } from "../ui/LoadingSpinner";
import { ROUTES } from "@/utils/constants";

export default function GitHubCallback() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get("code");
    const returnedState = urlParams.get("state");
    const storedState = sessionStorage.getItem("gh_oauth_state");

    if (!code) {
      ShowToast({
        description: "GitHub login failed: No code found",
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
    dispatch(githubLoginUser({ code, state: stateToSend, navigate }));
  }, []);

  return <LoadingSpinner size={62} />;
}
