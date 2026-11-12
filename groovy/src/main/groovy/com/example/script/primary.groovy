package com.example.script

import com.example.tool.ToolB

ToolB tool = new ToolB()
tool.methodB($logger)
$composeView.create { slot ->
    def state = slot.remember { slot.mutableStateOf(0) }
    slot.Column { column ->
        column.setModifier { modifier -> modifier.fillMaxSize(1.0f) }
        column.setHorizontalAlignment { alignments -> alignments.getCenterHorizontally() }
        column.setVerticalArrangement { arrangements -> arrangements.getCenter() }
        column.setContent { content ->
            content.Text { text ->
                text.setText($stringResources.getString("demo.counter.label") + state.getValue())
            }
            content.TextButton { button ->
                button.setContent { row ->
                    row.Text { text -> text.setText($stringResources.getString("demo.counter.button")) }
                }
                button.onClick { state.setValue(state.getValue() + 1) }
            }
            content.Text { text -> text.setText($stringResources.getString("demo.language")) }
        }
    }
}
$activity.start()
