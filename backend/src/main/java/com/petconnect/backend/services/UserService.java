package com.petconnect.backend.services;

import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;

import java.util.Optional;

public interface UserService {

    /**
     * Registers a new user.
     * @param user the user to register.
     * @return the registered user.
     */
    void registerUser(User user);

    /**
     * Finds a user by email.
     * @param email the email of the user to find.
     * @return an Optional containing the found user, or empty if not found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by ID.
     * @param userId the ID of the user to find.
     * @return an Optional containing the found user, or empty if not found.
     */
    Optional<User> findById(Long userId);

    /**
     * Verifies a user's account.
     * @param verificationToken the verification token.
     * @return true if the user was verified, false otherwise.
     */
    boolean verifyUser(String verificationToken);

    /**
     * Resets a user's password.
     * @param resetToken the reset token.
     * @param newPassword the new password.
     * @return true if the password was reset, false otherwise.
     */
    boolean resetPassword(String resetToken, String newPassword);

    /**
     * Updates user details.
     * @param user the user to update.
     * @return the updated user.
     */
    User updateUser(User user);

    /**
     * Deletes a user by ID.
     * @param userId the ID of the user to delete.
     */
    void deleteUser(Long userId);

    /**
     * Adds a role to a user.
     * @param userId the ID of the user.
     * @param role the role to add.
     * @return the updated user.
     */
    User addRoleToUser(Long userId, Role.RoleName role);

    /**
     * Removes a role from a user.
     * @param userId the ID of the user.
     * @param role the role to remove.
     * @return the updated user.
     */
    User removeRoleFromUser(Long userId, Role.RoleName role);

    /**
     * Authenticates a user.
     * @param email the user's email.
     * @param password the user's password.
     * @return an Optional containing the authenticated user, or empty if authentication failed.
     */
    Optional<User> authenticateUser(String email, String password);

    /**
     * Sends a verification email to the user.
     * @param user the user to send the verification email to.
     */
    void sendVerificationEmail(User user);

}
