-- phpMyAdmin SQL Dump
-- version 4.9.7
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Czas generowania: 25 Sie 2024, 13:45
-- Wersja serwera: 10.4.28-MariaDB-cll-lve
-- Wersja PHP: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `lbiio_quick_adoption_app`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `announcement`
--

CREATE TABLE `announcement` (
  `AnnouncementID` int(11) NOT NULL,
  `AnimalName` varchar(30) NOT NULL,
  `Species` varchar(50) NOT NULL,
  `Breed` varchar(50) NOT NULL,
  `DateRange` varchar(30) NOT NULL,
  `Food` varchar(50) NOT NULL,
  `AnimalImage` varchar(400) NOT NULL,
  `AnimalDescription` text NOT NULL,
  `OwnerID` varchar(30) NOT NULL,
  `AssignedKeeperID` varchar(30) DEFAULT NULL,
  `HasNewOffer` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `announcementstochats`
--

CREATE TABLE `announcementstochats` (
  `ChatID` varchar(20) NOT NULL,
  `AnnouncementID` int(11) NOT NULL,
  `LastMessageContent` varchar(255) NOT NULL,
  `LastMessageContentType` varchar(20) NOT NULL,
  `LastMessageTimestamp` varchar(30) NOT NULL,
  `LastMessageAuthor` varchar(30) NOT NULL,
  `IsChatAccepted` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `opinion`
--

CREATE TABLE `opinion` (
  `OpinionID` int(11) NOT NULL,
  `AuthorID` varchar(30) NOT NULL,
  `Content` varchar(255) NOT NULL,
  `Timestamp` varchar(30) NOT NULL,
  `RateStars` tinyint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `opinionstousers`
--

CREATE TABLE `opinionstousers` (
  `OpinionsToUsersID` int(11) NOT NULL,
  `OpinionID` int(11) NOT NULL,
  `ReceiverID` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user`
--

CREATE TABLE `user` (
  `UID` varchar(30) NOT NULL,
  `EmailAddress` varchar(50) NOT NULL,
  `Phone` varchar(20) NOT NULL,
  `Name` varchar(30) NOT NULL,
  `Surname` varchar(50) NOT NULL,
  `Country` varchar(50) NOT NULL,
  `City` varchar(50) NOT NULL,
  `Address` varchar(50) NOT NULL,
  `PostalCode` varchar(20) NOT NULL,
  `UserDescription` text NOT NULL,
  `ProfileImage` varchar(400) NOT NULL,
  `MaxReliability` int(11) NOT NULL,
  `AcquiredReliability` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `userstochats`
--

CREATE TABLE `userstochats` (
  `UsersToChatsID` int(11) NOT NULL,
  `ChatID` varchar(20) NOT NULL,
  `UID` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


--
-- Indeksy dla tabeli `announcement`
--
ALTER TABLE `announcement`
  ADD PRIMARY KEY (`AnnouncementID`),
  ADD KEY `OwnerID` (`OwnerID`),
  ADD KEY `AssignedKeeperID` (`AssignedKeeperID`);

--
-- Indeksy dla tabeli `announcementstochats`
--
ALTER TABLE `announcementstochats`
  ADD PRIMARY KEY (`ChatID`),
  ADD KEY `AnnouncementID` (`AnnouncementID`),
  ADD KEY `LastMessageAuthor` (`LastMessageAuthor`),
  ADD KEY `LastMessageAuthor_2` (`LastMessageAuthor`);

--
-- Indeksy dla tabeli `opinion`
--
ALTER TABLE `opinion`
  ADD PRIMARY KEY (`OpinionID`),
  ADD KEY `AuthorID` (`AuthorID`);

--
-- Indeksy dla tabeli `opinionstousers`
--
ALTER TABLE `opinionstousers`
  ADD PRIMARY KEY (`OpinionsToUsersID`),
  ADD KEY `OpinionID` (`OpinionID`),
  ADD KEY `UID` (`ReceiverID`);

--
-- Indeksy dla tabeli `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`UID`);

--
-- Indeksy dla tabeli `userstochats`
--
ALTER TABLE `userstochats`
  ADD PRIMARY KEY (`UsersToChatsID`),
  ADD KEY `UID` (`UID`),
  ADD KEY `ChatID` (`ChatID`);

--
-- AUTO_INCREMENT dla zrzuconych tabel
--

--
-- AUTO_INCREMENT dla tabeli `announcement`
--
ALTER TABLE `announcement`
  MODIFY `AnnouncementID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT dla tabeli `opinionstousers`
--
ALTER TABLE `opinionstousers`
  MODIFY `OpinionsToUsersID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=454;

ALTER TABLE `opinion`
  MODIFY `OpinionID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=454;

--
-- AUTO_INCREMENT dla tabeli `userstochats`
--
ALTER TABLE `userstochats`
  MODIFY `UsersToChatsID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `announcement`
--
ALTER TABLE `announcement`
  ADD CONSTRAINT `announcement_ibfk_1` FOREIGN KEY (`OwnerID`) REFERENCES `user` (`UID`),
  ADD CONSTRAINT `announcement_ibfk_2` FOREIGN KEY (`AssignedKeeperID`) REFERENCES `user` (`UID`);

--
-- Ograniczenia dla tabeli `announcementstochats`
--
ALTER TABLE `announcementstochats`
  ADD CONSTRAINT `announcementstochats_ibfk_1` FOREIGN KEY (`AnnouncementID`) REFERENCES `announcement` (`AnnouncementID`),
  ADD CONSTRAINT `announcementstochats_ibfk_2` FOREIGN KEY (`LastMessageAuthor`) REFERENCES `user` (`UID`);

--
-- Ograniczenia dla tabeli `opinion`
--
ALTER TABLE `opinion`
  ADD CONSTRAINT `opinion_ibfk_1` FOREIGN KEY (`AuthorID`) REFERENCES `user` (`UID`);

--
-- Ograniczenia dla tabeli `opinionstousers`
--
ALTER TABLE `opinionstousers`
  ADD CONSTRAINT `opinionstousers_ibfk_1` FOREIGN KEY (`OpinionID`) REFERENCES `opinion` (`OpinionID`),
  ADD CONSTRAINT `opinionstousers_ibfk_2` FOREIGN KEY (`ReceiverID`) REFERENCES `user` (`UID`);

--
-- Ograniczenia dla tabeli `userstochats`
--
ALTER TABLE `userstochats`
  ADD CONSTRAINT `userstochats_ibfk_1` FOREIGN KEY (`UID`) REFERENCES `user` (`UID`),
  ADD CONSTRAINT `userstochats_ibfk_2` FOREIGN KEY (`ChatID`) REFERENCES `announcementstochats` (`ChatID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
