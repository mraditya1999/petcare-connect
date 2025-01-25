import { useRouteError } from "react-router-dom";

const ErrorElement = () => {
  const error = useRouteError();
  console.log(error);

  return (
    <h4 className="text-4xl font-bold">
      Something went wrong, Please try again later...{" "}
    </h4>
  );
};
export default ErrorElement;
