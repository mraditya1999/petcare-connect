import { Button, ButtonProps } from "../ui/button";

interface CustomButtonProps extends ButtonProps {
  buttonText?: string;
}

const CustomButton: React.FC<CustomButtonProps> = (props) => {
  const { buttonText, children, className, ...rest } = props;

  return (
    <Button {...rest} className={`rounded-full ${className}`}>
      {buttonText || children}
    </Button>
  );
};

export default CustomButton;
