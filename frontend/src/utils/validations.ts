import { z } from "zod";

export const loginFormSchema = z.object({
  email: z
    .string()
    .nonempty("Email is required")
    .email({ message: "Invalid email format" }),
  password: z
    .string()
    .nonempty("Password is required")
    .min(8, { message: "Password must be at least 8 characters" })
    .regex(
      /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
      {
        message:
          "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character",
      },
    ),
});

export const registerFormSchema = z.object({
  firstName: z
    .string()
    .nonempty("First Name is required")
    .min(3, "First Name must be at least 3 characters")
    .max(50, "First Name must be at most 50 characters long"),
  lastName: z
    .string()
    .nonempty("Last Name is required")
    .min(3, "Last Name must be at least 3 characters")
    .max(50, "Last Name must be at most 50 characters long"),
  email: z
    .string()
    .nonempty("Email is required")
    .email({ message: "Invalid email format" }),
  password: z
    .string()
    .nonempty("Password is required")
    .min(8, { message: "Password must be at least 8 characters" })
    .regex(
      /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
      {
        message:
          "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character",
      },
    ),
});

export const forgetPasswordFormSchema = z.object({
  email: z.string().nonempty("Email is required").email("Invalid email format"),
});

export const resetPasswordFormSchema = z
  .object({
    password: z
      .string()
      .nonempty("Password is required")
      .min(8, { message: "Password must be at least 8 characters" })
      .regex(
        /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
        {
          message:
            "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character",
        },
      ),
    confirmPassword: z
      .string()
      .nonempty("Confirm Password is required")
      .min(8, { message: "Confirm Password must be at least 8 characters" })
      .regex(
        /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
        {
          message:
            "Confirm Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character",
        },
      ),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  });

export const profileFormSchema = z.object({
  firstName: z.string().nonempty("First name is required"),
  lastName: z.string().nonempty("Last name is required"),
  email: z
    .string()
    .email("Invalid email address")
    .nonempty("Please enter a valid email address"),
  mobileNumber: z
    .union([
      z.string().regex(/^\d{10}$/, "Mobile number must be exactly 10 digits"),
      z.null(),
    ])
    .optional(),
  pincode: z
    .string()
    .regex(/^\d{6}$/, "Pincode must be exactly 6 digits")
    .optional(),
  city: z.string().optional(),
  state: z.string().optional(),
  country: z.string().optional(),
  locality: z.string().optional(),
});


export const updatePasswordSchema = z
  .object({
    currentPassword: z.string().min(1, "Current password is required"),
    newPassword: z
      .string()
      .min(8, "Password must be at least 8 characters long")
      .regex(/[A-Z]/, "Must contain at least one uppercase letter")
      .regex(/[0-9]/, "Must contain at least one number"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  });
