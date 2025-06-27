package com.jhonatha.soccerspider

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Dados do Jogo ---
data class Match(
    val team1: String,
    val team2: String,
    var score1: String = "",
    var score2: String = ""
) {
    
    fun winner(): String? {
        return try {
            val s1 = score1.toInt()
            val s2 = score2.toInt()
            when {
                s1 > s2 -> team1
                s2 > s1 -> team2
                else -> null // empate
            }
        } catch (e: NumberFormatException) {
            null
        }
    }
}

// --- Tela de Chaveamento ---
@Composable
fun BracketScreen(teams: List<String>, onBack: () -> Unit) {
    val shuffledTeams = remember { teams.shuffled() }

    val round1Matches = remember {
        shuffledTeams.chunked(2).map {
            Match(it.getOrElse(0) { "?" }, it.getOrElse(1) { "?" })
        }
    }

    val round2Matches = remember { mutableStateListOf<Match>() }
    val round3Matches = remember { mutableStateListOf<Match>() }

    fun updateNextRound(currentRound: List<Match>, nextRound: SnapshotStateList<Match>) {
        val winners = currentRound.mapNotNull { it.winner() }
        nextRound.clear()
        winners.chunked(2).forEach {
            val t1 = it.getOrElse(0) { "?" }
            val t2 = it.getOrElse(1) { "?" }
            nextRound.add(Match(t1, t2))
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("🏆 Chaveamento", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            item { Text("🔸 Quartas de final", fontSize = 18.sp) }
            items(round1Matches) { match ->
                MatchInput(match) { updateNextRound(round1Matches, round2Matches) }
            }

            if (round2Matches.isNotEmpty()) {
                item { Text("🔸 Semifinal", fontSize = 18.sp, modifier = Modifier.padding(top = 12.dp)) }
                items(round2Matches) { match ->
                    MatchInput(match) { updateNextRound(round2Matches, round3Matches) }
                }
            }

            if (round3Matches.isNotEmpty()) {
                item { Text("🔸 Final", fontSize = 18.sp, modifier = Modifier.padding(top = 12.dp)) }
                items(round3Matches) { match ->
                    MatchInput(match)
                }
            }

            if (round3Matches.firstOrNull()?.winner() != null) {
                item {
                    Text(
                        "🎉 Campeão: ${round3Matches.first().winner()}",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}

// --- Entrada de dados para cada jogo ---
@Composable
fun MatchInput(match: Match, onScoreChanged: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row {
            OutlinedTextField(
                value = match.score1,
                onValueChange = {
                    match.score1 = it.filter { c -> c.isDigit() }
                    onScoreChanged?.invoke()
                },
                label = { Text("Gols ${match.team1}") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )

            OutlinedTextField(
                value = match.score2,
                onValueChange = {
                    match.score2 = it.filter { c -> c.isDigit() }
                    onScoreChanged?.invoke()
                },
                label = { Text("Gols ${match.team2}") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        val winner = match.winner()
        if (winner != null) {
            Text("✅ Vencedor: $winner", modifier = Modifier.padding(top = 4.dp))
        }
    }
}






