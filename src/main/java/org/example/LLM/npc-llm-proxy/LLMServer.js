
const express = require('express');
const axios = require('axios');
const app = express();
app.use(express.json());

const OPENROUTER_KEY = "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";
const MODEL = 'openrouter/horizon-beta';

app.post('/npc-dialog', async (req, res) => {
    const { npcName, playerMessage, characteristics, information } = req.body;

    const prompt = `
    You are an NPC named ${npcName} in Stardew-valley game..
    Your traits: ${characteristics}.
    Environment information: ${information}.
    Player message / Situation: "${playerMessage}".
    Respond in character based on your characteristics, weather, season, and other information.`.trim();
    `;

    try {
        const response = await axios.post(
            'https://openrouter.ai/api/v1/chat/completions',
            {
                model: MODEL,
                messages: [{role: 'user', content: prompt}],
            },
            {
                headers: {
                    'Authorization': `Bearer ${OPENROUTER_KEY}`,
                    'Content-Type': 'application/json',
                }
            }
        );

        const npcReply = response.data.choices[0].message.content;
        res.json({ reply: npcReply });
    } catch (error) {
        console.log(error?.response?.data || error.message);
        res.status(500).json({ error: "Failed to get NPC response." });
    }
})

app.listen(8080, () => console.log("NPC LLM proxy running at http://localhost:8080"));