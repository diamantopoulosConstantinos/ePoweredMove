package com.kosdiam.epoweredmove.models.enums

import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.interfaces.IEnumName

enum class MessageType (override val stringResource: Int) : IEnumName {
    GENERAL_ERROR(R.string.general_error),
    NO_INTERNET(R.string.no_internet),
    EMPTY_CREDENTIALS(R.string.empty_credentials),
    ERROR_CREATE_ACCOUNT(R.string.create_account_error),
    EMPTY_EMAIL(R.string.empty_email),
    GPS_NOT_ENABLED(R.string.gps_not_enabled),
    SENT_EMAIL_RESET_PASS_SUCCESS(R.string.reset_email_sent),
    GPS_NO_PERMISSION(R.string.gps_no_permission),
    STORAGE_NO_PERMISSION(R.string.storage_no_permission),
    NO_LOCATION_SELECTED(R.string.no_location_selected),
    UPLOAD_IMAGE_ERROR(R.string.upload_image_error),
    ERROR_ADDING_RESERVATION_MISSING_FIELDS(R.string.error_adding_reservation_missing_fields),
    DOWNLOAD_IMAGE_ERROR(R.string.download_image_error),
    NO_LOCATION_FOUND(R.string.no_location_found),
    ERROR_INVALID_CUSTOM_TOKEN(R.string.general_error),
    ERROR_CUSTOM_TOKEN_MISMATCH(R.string.general_error),
    ERROR_INVALID_CREDENTIAL(R.string.wrong_credentials),
    ERROR_WRONG_PASSWORD(R.string.wrong_credentials),
    ERROR_USER_MISMATCH(R.string.general_error),
    ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL(R.string.wrong_credentials),
    ERROR_CREDENTIAL_ALREADY_IN_USE(R.string.username_is_used),
    ERROR_USER_TOKEN_EXPIRED(R.string.general_error),
    GOOGLE_SIGN_IN_FAILED(R.string.google_sign_in_failed),
    ERROR_INVALID_USER_TOKEN(R.string.general_error),
    ERROR_OPERATION_NOT_ALLOWED(R.string.general_error),
    ERROR_FETCHING_USER(R.string.error_fetching_user),
    ERROR_FETCHING_RESERVATIONS(R.string.error_fetching_reservations),
    ERROR_SEARCHING_CHARGING_STATION(R.string.error_searching_charging_station),
    NO_CHARGING_STATIONS_FOUND(R.string.no_charging_stations_found),
    CLOSEST_CHARGING_STATION_FOUND(R.string.closest_charging_station_found),
    ERROR_NO_VEHICLE_SELECTED(R.string.error_no_vehicle_selected),
    ERROR_FETCHING_POIS(R.string.error_fetching_pois),
    ERROR_FETCHING_VEHICLES(R.string.error_fetching_vehicles),
    ERROR_DELETING_VEHICLE(R.string.error_deleting_vehicle),
    ERROR_CANCELLING_RESERVATION(R.string.error_cancelling_reservation),
    ERROR_FETCHING_CLOSEST_ROUTE_INFO(R.string.error_fetching_closest_route_info),
    ERROR_ADDING_VEHICLE(R.string.error_adding_vehicle),
    ERROR_EDITING_VEHICLE(R.string.error_editing_vehicle),
    ERROR_ADDING_RESERVATION(R.string.error_adding_reservation),
    ERROR_ADDING_RESERVATION_TIME_OVERLAP(R.string.error_adding_reservation_time_overlap),
    RESERVATION_ADDED(R.string.reservation_added),
    ERROR_FETCHING_PLUGS(R.string.error_fetching_plugs),
    NO_PLUGS_FOUND(R.string.no_plugs_found),
    ERROR_ADDING_REVIEW(R.string.error_adding_review),
    ERROR_FETCHING_REVIEWS(R.string.error_fetching_reviews),
    ERROR_NO_PLUG_FOUND_FOR_SELECTED_VEHICLE(R.string.error_no_plug_found_for_selected_vehicle),
    ERROR_FETCHING_PLUG_TYPES(R.string.error_fetching_plug_types);

    companion object {
        fun from(type: String?): MessageType = values().find { it.name == type } ?: GENERAL_ERROR
    }
}