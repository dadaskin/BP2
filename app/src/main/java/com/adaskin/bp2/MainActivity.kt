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
import androidx.compose.runtime.saveable.rememberSaveable
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
    val sdf = SimpleDateFormat( "dd-MMM-yy     HH:mm")
    return sdf.format(Date())
}

@Composable
fun MainScreen(name: String, modifier: Modifier = Modifier) {
    var systolicValue by rememberSaveable { mutableStateOf("")}
    var diastolicValue by rememberSaveable { mutableStateOf("")}
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
                clickHandler(context, nowString, systolicValue, diastolicValue)
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

private fun clickHandler(context:Context, dateString:String, systolic:String, diastolic:String) {
    Toast.makeText(context, "In clickHandler()", Toast.LENGTH_LONG).show()

    // writeInfoToFile(context, dateString, systolic, diastolic)
}

private fun writeInfoToFile(context: Context, dateString:String, systolic:String, diastolic:String) {
    val msg = "$dateString  $systolic/$diastolic\n"
    val filename = "bpmeas3.txt"
    val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    Log.i("Foo", "Writing to: $documentsDir")
    val file : File
    try {
        file = File(documentsDir, filename)
        try {
            if (!file.exists()) {
                Log.i("Foo", "File does not exist, try creating a new one.")
                file.createNewFile()
                Log.i("Foo", "Got past createNewFile()")
            }
        } catch (e:Exception) {
            Log.i("Foo", "Exists() and Create(): " + e.message)
        }
        try {
            Log.i("Foo", "Try to write some text to the file")
            file.writeText(msg)
            Log.i("Foo", "Got past the writeText().")
        } catch (e:Exception) {
            Log.i("Foo", "WriteText(): " + e.message)
        }
    } catch (e:Exception) {
        Log.i("Foo", "Create File object" + e.message)
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