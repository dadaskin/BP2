package com.adaskin.bp2

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.adaskin.bp2.ui.theme.BP2Theme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BP2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        name = "Blood Pressure",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
private fun getNow():String {
    // "hh:mm a" gets 12 hr time format, "HH:mm" for 24-hour time format
    val sdf = SimpleDateFormat( "dd-MMM-yy     hh:mm a")
    return sdf.format(Date())
}

@Composable
fun MainScreen(name: String, modifier: Modifier = Modifier) {
    var systolicValue by remember { mutableStateOf("")}
    var diastolicValue by remember { mutableStateOf("")}
    var isEnabled by remember {mutableStateOf(systolicValue.isNotEmpty() && diastolicValue.isNotEmpty())}
    val nowString = getNow()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Text(
            text = name,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Text (
            text = nowString,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center
        )
        Row{
            TextField(
                value = systolicValue,
                onValueChange = {
                    systolicValue = it
                    isEnabled = systolicValue.isNotEmpty() && diastolicValue.isNotEmpty()
                },
                modifier = Modifier.width(150.dp).padding(16.dp),
                label = {
                    Text(
                        text = "Systolic",
                        fontStyle = FontStyle.Italic
                    )
                }
            )
            Text(
                "/",
                modifier = Modifier
                    .width(20.dp)
                    .padding(
                        top = 30.dp,
                        start = 10.dp,
                        end = 10.dp
                    ),
            )
            TextField(
                value = diastolicValue,
                onValueChange = {
                    diastolicValue = it
                    isEnabled = systolicValue.isNotEmpty() && diastolicValue.isNotEmpty()
                },
                modifier = Modifier.width(150.dp).padding(16.dp),
                label = {
                    Text(
                        text = "Diastolic",
                        fontStyle = FontStyle.Italic
                    )
                },
            )
        }
        // Style the button
        ElevatedButton(
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth(),
            shape = ButtonDefaults.elevatedShape,
            colors = ButtonDefaults.elevatedButtonColors(),
            onClick = {
                writeInfoToFile(context, nowString, systolicValue, diastolicValue)
                isEnabled = !isEnabled
            }
        ) {
            Text(
                text = "Save to file",
                modifier = Modifier.width(200.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun writeInfoToFile(context: Context, dateString:String, systolic:String, diastolic:String) {
    val msg = "$dateString  $systolic/$diastolic\n"
    val filename = "bpmeas.txt"
    try {
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentsDir, filename)
        if (!file.exists()) {
            file.createNewFile()
        }
        file.appendText(msg, Charsets.UTF_8)
    } catch (e:Exception) {
        Log.i("Foo", "Some problem with file:\n" + e.message)
    }

    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BP2Theme {
        MainScreen("Android BP 2")
    }
}