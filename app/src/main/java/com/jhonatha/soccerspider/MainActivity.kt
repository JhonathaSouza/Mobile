package com.jhonatha.soccerspider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhonatha.soccerspider.ui.theme.SoccerSpiderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoccerSpiderTheme {
                var showBracket by remember { mutableStateOf(false) }
                var teamList by remember { mutableStateOf(listOf<String>()) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showBracket) {
                        BracketScreen(teams = teamList) {
                            showBracket = false
                        }
                    } else {
                        TeamEntryScreen( // <<-- É aqui que o erro está acontecendo
                            teamList = teamList,
                            onAddTeam = { teamList = teamList + it },
                            onGenerate = {
                                if (teamList.size >= 2) {
                                    showBracket = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamEntryScreen(
    teamList: List<String>,
    onAddTeam: (String) -> Unit,
    onGenerate: () -> Unit
) {
    var teamName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chaveamento de Times",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = teamName,
            onValueChange = { teamName = it },
            label = { Text("Nome do time") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (teamName.isNotBlank()) {
                    onAddTeam(teamName.trim())
                    teamName = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar Time")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Times Adicionados:", fontSize = 18.sp)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(teamList) { team ->
                Text(
                    text = team,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Button(
            onClick = { onGenerate() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gerar Chaveamento")
        }
    }
}


