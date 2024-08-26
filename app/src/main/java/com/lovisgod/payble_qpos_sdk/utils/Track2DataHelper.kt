package com.lovisgod.payble_qpos_sdk.utils



class Track2DataHelper {
    var track2: String? = null
    var panData: String? = null
    var wasFallback: Boolean = false
    var cardExpiry : String = ""
    var srcPayble : String = ""

    companion object {
        fun create(track2Data: String) = Track2DataHelper().apply {
//            val track2data =iccDataInfo.TRACK_2_DATA
            println("track2 data => ${track2Data}")
            // extract pan and expiry
            val strTrack2 = track2Data.split("F")[0]
            var panX = strTrack2.split("D")[0]
            val expiry = strTrack2.split("D")[1].substring(0, 4)
            val src = strTrack2.split("D")[1].substring(4, 7)

            cardExpiry = expiry
            srcPayble = src
            panData = panX
            track2 = let {
                    val neededLength = strTrack2.length - 2
                    val isVisa = strTrack2.startsWith('4')
                    val hasCharacter = strTrack2.last().isLetter()

                    // remove character suffix for visa
                    if (isVisa && hasCharacter) strTrack2.substring(0..neededLength)
                    else strTrack2
                }
            }

        }
    }