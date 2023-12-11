export const STORAGE_KEYS = {
  /**
   * This constant is used to store and retrieve the access token from a storage mechanism.
   * The access token is typically used for authentication purposes in an application.
   */
  TOKEN: 'ACCESSIBILATOR@TOKEN',

  /**
   * This constant is used to store and retrieve the expiry time of the token.
   * Keeping track of the expiry time is important to ensure that the application requests a new token when the current one expires.
   */
  EXPIRY: 'ACCESSIBILATOR@EXPIRY',

  /**
   * This constant is used to store and retrieve user information.
   * This includes the details such as the user's ID, name, or other personal information.
   */
  USER: 'ACCESSIBILATOR@USER',
};
