package sol_game

import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.module.kotlin.*
import sol_engine.utils.ResourceUtils
import sol_game.core_game.AbilityType
import sol_game.core_game.CharacterConfig
import java.io.File


class CharacterJsonDeserializer {
    companion object {


        fun fromFile(filename: String): CharacterConfig {

            val configStr = ResourceUtils.loadResourceAsString(filename)
            val mapper = jacksonObjectMapper()
            val config: CharacterConfig = mapper.readValue(configStr)
            return config
        }


    }
}

