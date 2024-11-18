package com.example.votingapplication

import junit.framework.TestCase.assertEquals
import org.junit.Test

class ModuleTestStaticClasses {

    @Test
    fun testElection() {
        val electionName = "Election 1"
        val startDate = "19/11/2024"
        val endDate = "20/11/2024"
        val results= ""
        val isDone = "false"
        val election = Election(electionName, startDate, endDate, results, isDone)
        assertEquals(electionName, election.electionName)
        assertEquals(startDate, election.startDate)
        assertEquals(endDate, election.endDate)
        assertEquals(results, election.results)
        assertEquals(isDone, election.isDone)
    }

    @Test
    fun testVoter() {
        val fName = "Swasti"
        val lName = "Swagat"
        val gender = "Male"
        val dob = "15/03/2005"
        val aadharNum = "222244445555"
        val hasSubmitted = "false"
        val isVerified = "true"
        val message = "Welcome!!"
        val userId = ""
        val voter = User(fName, lName, gender, dob, aadharNum, hasSubmitted, isVerified, message, userId)
        assertEquals(fName, voter.fName)
        assertEquals(lName, voter.lName)
        assertEquals(gender, voter.gender)
        assertEquals(dob, voter.dob)
        assertEquals(aadharNum, voter.aadharNum)
        assertEquals(hasSubmitted, voter.hasSubmitted)
        assertEquals(isVerified, voter.isVerified)
        assertEquals(message, voter.message)
        assertEquals(userId, voter.userId)
      }

    @Test
    fun testCandidate() {
        val fName = "Ayush"
        val lName = "Ashutosh"
        val pName = "BJP"
        val gender = "Male"
        val dob = "11/03/2004"
        val aadharNum = "222244445555"
        val hasSubmitted = "false"
        val isVerified = "true"
        val message = ""
        val userId = ""
        val candidate = Candidate(fName, lName, gender, pName, dob, aadharNum, hasSubmitted, isVerified, message, userId)
        assertEquals(fName, candidate.fName)
        assertEquals(lName, candidate.lName)
        assertEquals(pName, candidate.pName)
        assertEquals(gender, candidate.gender)
        assertEquals(dob, candidate.dob)
        assertEquals(aadharNum, candidate.aadharNum)
        assertEquals(hasSubmitted, candidate.hasSubmitted)
        assertEquals(isVerified, candidate.isVerified)
        assertEquals(message, candidate.message)
        assertEquals(userId, candidate.userId)
    }
}