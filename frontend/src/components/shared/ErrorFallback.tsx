import { FC } from "react";
import { Button } from "../ui/button";

export interface ErrorFallbackProps {
  message: string;
  onRetry?: () => void;
  retryLabel?: string;
  className?: string;
}

const ErrorFallback: FC<ErrorFallbackProps> = ({
  message,
  onRetry,
  retryLabel = "Try Again",
  className = "",
}) => {
  return (
    <div
      className={`rounded-xl border border-red-300 bg-red-50 p-4 text-center text-sm text-red-700 dark:border-red-700 dark:bg-red-900 dark:text-red-200 ${className}`}
    >
      <p className="mb-2">{message}</p>

      {onRetry && (
        <Button
          onClick={onRetry}
          className="bg-red-600 text-white hover:bg-red-700 dark:bg-red-400 dark:text-gray-900 dark:hover:bg-red-500"
        >
          {retryLabel}
        </Button>
      )}
    </div>
  );
};

export default ErrorFallback;
