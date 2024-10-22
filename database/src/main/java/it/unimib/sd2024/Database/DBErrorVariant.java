package it.unimib.sd2024.Database;

public enum DBErrorVariant {
	CannotCreateDirectoryStructure,
	UserAlreadyExists,
	InvalidNumberOfParameters,
	DomainAlreadyRegistered,
	OrderAlreadyExists,
	InvalidOrderType,
	UserDoesNotExists,
	CannotCreateFile,
	DomainOwnedByDifferentUser,
	DomainNotRegistered,
	RenewalRequestedForExpiredDomain,
	RenewedForMoreThanTenYears,
	RegisteredForMoreThanTenYears,
	NoDomainRegistered,
}
