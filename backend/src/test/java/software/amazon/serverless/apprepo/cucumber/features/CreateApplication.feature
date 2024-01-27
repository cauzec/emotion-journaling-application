Feature: CreateApplication
  As a user, I would like to create a therapist account.

  Scenario: User creates a therapist account
    When a user creates a therapist account
    Then a new therapist account should be created

  Scenario: User creates a therapist account with the same id
    Given a user has a therapist account
    When the user creates a therapist account with the same id
    Then the call should fail because the therapist account already exists

  Scenario: User creates a therapist account without therapist account id
    When a user creates a therapist account without therapist account id
    Then the call should fail because of bad request

  Scenario: User creates a therapist account without therapistName
    When a user creates a therapist account without therapistName
    Then the call should fail because of bad request

  Scenario: User creates a therapist account without therapistType
    When a user creates a therapist account without therapistType
    Then the call should fail because of bad request

  Scenario: User creates a therapist account with invalid therapist account id
    When a user creates a therapist account with invalid therapist account id
    Then the call should fail because of bad request

  Scenario: User creates a therapist account with invalid therapistName
    When a user creates a therapist account with invalid therapistName
    Then the call should fail because of bad request

  Scenario: User creates a therapist account with invalid therapistType
    When a user creates a therapist account with invalid therapistType
    Then the call should fail because of bad request

  Scenario: User creates a therapist account with invalid therapistArea
    When a user creates a therapist account with invalid therapistArea
    Then the call should fail because of bad request