const uid =  Number(document.getElementById("uid_from_session").dataset.userId)

document.getElementById('translate-btn').addEventListener('click', async function() {
    const text = document.getElementById('input-text').value
    const destLan = document.getElementById("target-language").value
    const srcLan = document.getElementById("source-language").value
    
    const response = await axios.post(
        '/usage/authorized', 
        {
            srcLan: srcLan,
            destLang: destLan,
            text: text,
            userId: uid
        }
    );
    console.log(response.data)
    document.getElementById("output-text").value = response.data[0]
})

document.getElementById('swap-languages').addEventListener('click', function() {
    console.log("languages swapped")
    let dest = document.getElementById("target-language")
    let src = document.getElementById("source-language")
    let tmp = src.value

    src.value = dest.value
    dest.value = tmp

    let destText = document.getElementById("input-text")
    let srcText = document.getElementById("output-text")
    tmp = srcText.value
    
    srcText.value = destText.value
    destText.value = tmp
})

document.getElementById('history-btn').addEventListener('click', async function() {
    const historyObj = document.getElementById("history-content")
    const response = await axios.get(
        `/translations/user/${uid}`
    )
    console.log(response)
    const transitions = response.data.reverse()
    let history_content = ''    // empty html string for later
    for (const transition of transitions) {
        history_content += `
        <div class="history-item">
            <div class="lang-group">
                <p class="history-item-src-text lang-box">
                    ${transition.originalLanguage}
                </p>
                <p class="history-item-dest-text lang-box">
                    ${transition.translationLanguage}
                </p>
            </div>
            <div class="trnslation-item-history">
                <p class="history-item-src-text translation-box">
                    ${transition.originalText}
                </p>
                <p class="history-item-dest-text translation-box">
                    ${transition.translatedText}
                </p>
            </div>
        </div>
        `
    }
    historyObj.innerHTML = history_content
    console.log("showing history")
    document.getElementsByClassName("history-overlay")[0].classList.add("active")
})

document.getElementById('close-history-btn').addEventListener('click', function() {
    console.log("hiding history")
    document.getElementsByClassName("history-overlay")[0].classList.remove("active")
})

